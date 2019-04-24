package com.jpp.mp.screens.main.userlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.paging.MPPagingDataSourceFactory
import com.jpp.mp.screens.SingleLiveEvent
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import java.util.concurrent.Executor
import com.jpp.mpdomain.usecase.account.GetFavoriteMoviesUseCase.FavoriteMoviesResult.*
import com.jpp.mpdomain.usecase.support.RefreshAppDataUseCase
import javax.inject.Inject

/**
 * [ViewModel] to support the different user movies sections in the account user flow.
 *
 * - Exposes an output in a LiveData object that receives [UserMoviesViewState] updates as soon
 * as any new state is identified by the ViewModel.
 * - Exposes a second output in a LiveData object that receives [UserMoviesViewNavigationEvent] updates
 * as soon as a new navigation event is detected from the UI.
 */
class UserMoviesViewModel @Inject constructor(private val favoritesMoviesUseCase: GetFavoriteMoviesUseCase,
                                              private val configMovieUseCase: ConfigMovieUseCase,
                                              private val refreshAppDataUseCase: RefreshAppDataUseCase,
                                              private val networkExecutor: Executor)
    : ViewModel() {

    private val viewState = MediatorLiveData<UserMoviesViewState>()
    private val navigationEvents by lazy { SingleLiveEvent<UserMoviesViewNavigationEvent>() }
    private lateinit var retryFunc: (() -> Unit)

    /**
     * Called on initialization of the UserMoviesFragment.
     * Each time this method is called, a new movie list will be fetched from the use case
     * and posted to the viewState, unless a previous list has been fetched.
     */
    fun fetchData(moviePosterSize: Int, movieBackdropSize: Int) {
        if (viewState.value is UserMoviesViewState.Loading
                || viewState.value is UserMoviesViewState.InitialPageLoaded) {
            return
        }


        viewState.value = UserMoviesViewState.Loading
        fetchFreshPage(moviePosterSize, movieBackdropSize)
        observeDataRefresh { fetchFreshPage(moviePosterSize, movieBackdropSize) }
    }

    /**
     * Exposes a stream that is updated with a new [UserMoviesViewState]
     * each time that a new state is identified.
     */
    fun viewState(): LiveData<UserMoviesViewState> = viewState

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<UserMoviesViewNavigationEvent> = navigationEvents

    /**
     * Attempts to execute the last movie fetching step that was executed. Typically called after an error
     * is detected.
     */
    fun retry() {
        retryFunc.invoke()
    }

    /**
     * Called when the user selects an item from the list being shown.
     */
    fun onItemSelected(item: UserMovieItem) {
        with(item) {
            navigationEvents.value = UserMoviesViewNavigationEvent.ToMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title
            )
        }
    }

    /**
     * Starts the process to create the the PagedList that will back the list of movies shown to the
     * user.
     * When the data retrieved from [createPagedList] is obtained, a new state is pushed to viewState().
     */
    private fun fetchFreshPage(moviePosterSize: Int,
                               movieBackdropSize: Int) {
        createPagedList(moviePosterSize, movieBackdropSize).let {
            viewState.addSource(it) { pagedList ->
                if (pagedList.size > 0) {
                    viewState.value = UserMoviesViewState.InitialPageLoaded(pagedList)
                } else {
                    retryFunc = {
                        fetchData(moviePosterSize, movieBackdropSize)
                    }
                }
            }
        }
    }

    /**
     * This is the method that creates the actual [PagedList] that will be used to provide
     * infinite scrolling. It uses a [MPPagingDataSourceFactory] that is mapped to have the
     * desired model ([UserMovieItem]) in order to create the [PagedList].
     *
     * The steps to create the proper instance of [PagedList] are:
     * 1 - Create a [MPPagingDataSourceFactory] instance of type [Movie].
     *      1.1. Create the function that will be used to retry the last movies fetching.
     * 2 - Map the created instance to a second DataSourceFactory of type [Movie], to
     *     execute the configurations of the images path of every result.
     * 3 - Map the instance created in (2) to a new DataSourceFactory that will map the
     *     [Movie] to a [MovieItem].
     */
    private fun createPagedList(moviePosterSize: Int,
                                movieBackdropSize: Int): LiveData<PagedList<UserMovieItem>> {
        return MPPagingDataSourceFactory<Movie> { page, callback -> fetchPage(page, callback) }
                .apply { retryFunc = { networkExecutor.execute { retryLast() } } }
                .map { configMovieUseCase.configure(moviePosterSize, movieBackdropSize, it) }
                .map { mapDomainMovie(it.movie) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
                }
    }

    /**
     * Fetches the movies's page indicated by [page] and invokes the provided [callback] when done.
     * - [page] indicates the current page number to retrieve.
     * - [callback] is a callback executed when the movie fetching us successful. The callback
     *   receives the list of [Movie]s retrieved and the index of the next movies page to fetch.
     * - if an error is detected, then the proper UI update is posted in viewState().
     */
    private fun fetchPage(page: Int, callback: (List<Movie>, Int) -> Unit) {
        favoritesMoviesUseCase
                .getUserFavoriteMovies(page)
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> {
                            viewState.postValue(if (page > 1) UserMoviesViewState.ErrorNoConnectivityWithItems else UserMoviesViewState.ErrorNoConnectivity)
                        }
                        is ErrorUnknown -> {
                            viewState.postValue(if (page > 1) UserMoviesViewState.ErrorUnknownWithItems else UserMoviesViewState.ErrorUnknown)
                        }
                        is UserNotLogged -> viewState.postValue(UserMoviesViewState.UserNotLogged)
                        is NoFavorites -> if (page == 1) viewState.postValue(UserMoviesViewState.NoMovies)
                        is Success -> callback(ucResult.moviesPage.results, page + 1)
                    }
                }
    }

    /**
     * The data shown in the section supported by this VM needs to be refreshed if the backing data
     * changes for some reason.
     * The VM starts observing the data refresh UC and if the user movies are updated, triggers doOnRefresh.
     */
    private fun observeDataRefresh(doOnRefresh: () -> Unit) {
        viewState.removeSource(refreshAppDataUseCase.appDataUpdates())
        viewState.addSource(refreshAppDataUseCase.appDataUpdates()) { dataRefresh ->
            when (dataRefresh) {
                is RefreshAppDataUseCase.AppDataRefresh.UserAccountMovies -> {
                    viewState.postValue(UserMoviesViewState.Refreshing)
                    doOnRefresh()
                }
                is RefreshAppDataUseCase.AppDataRefresh.LanguageChanged -> {
                    viewState.postValue(UserMoviesViewState.Refreshing)
                    doOnRefresh()
                }
            }
        }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        UserMovieItem(
                movieId = id,
                headerImageUrl = backdrop_path ?: "emptyPath",
                title = title,
                contentImageUrl = poster_path ?: "emptyPath"
        )
    }

}
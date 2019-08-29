package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.CoroutineExecutor
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.*
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToUserAccount
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] used to support the user's movie list section of the application.
 * Produces different [UserMovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 *
 * Since the UI is using the Android Paging Library, the VM needs a way to map the data retrieved from
 * the [UserMovieListInteractor] to a [PagedList] that can be used by the library. That process is done
 * using the [MPPagingDataSourceFactory] that creates the DataSource and produces a [LiveData] object
 * that is combined with the [viewStates] in order to properly map the data into a [UserMovieListViewState].
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class UserMovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                 private val userMovieListInteractor: UserMovieListInteractor,
                                                 private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates = MediatorLiveData<UserMovieListViewState>()
    val viewStates: LiveData<UserMovieListViewState> get() = _viewStates

    private val _currentSection = MutableLiveData<UserMovieListType>()
    val currentSection: LiveData<UserMovieListType> get() = _currentSection

    private val _navEvents = SingleLiveEvent<UserMovieListNavigationEvent>()
    val navEvents: LiveData<UserMovieListNavigationEvent> get() = _navEvents

    private lateinit var currentParam: UserMovieListParam

    private val retry: () -> Unit = {
        postLoadingAndInitializePagedList(
                currentParam.posterSize,
                currentParam.backdropSize,
                currentParam.section
        )
    }

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(userMovieListInteractor.userAccountEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = UserMovieListViewState.showNoConnectivityError(retry)
                is UnknownError -> _viewStates.value = UserMovieListViewState.showUnknownError(retry)
                is UserNotLogged -> _navEvents.value = GoToUserAccount
                is UserChangedLanguage -> refreshData()
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: UserMovieListParam) {
        currentParam = param
        _currentSection.value = currentParam.section
        postLoadingAndInitializePagedList(
                currentParam.posterSize,
                currentParam.backdropSize,
                currentParam.section
        )
    }


    /**
     * Called when an item is selected in the list of movies.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onMovieSelected(movieItem: UserMovieItem, positionInList: Int) {
        with(movieItem) {
            _navEvents.value = UserMovieListNavigationEvent.GoToMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title,
                    positionInList = positionInList)
        }
    }


    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [UserMovieItem] that will be rendered by the view layer.
     */
    private fun postLoadingAndInitializePagedList(posterSize: Int,
                                                  backdropSize: Int,
                                                  listType: UserMovieListType) {
        _viewStates.value = UserMovieListViewState.showLoading()
        _viewStates.addSource(createPagedList(posterSize, backdropSize, listType)) { pagedList ->
            if (pagedList.isNotEmpty()) {
                _viewStates.value = UserMovieListViewState.showMovieList(pagedList)
            }
        }
    }

    /**
     * Creates a [LiveData] object of the [PagedList] that is used to wire up the Android Paging Library
     * with the interactor in order to fetch a new page of movies each time the user scrolls down in
     * the list of movies.
     */
    private fun createPagedList(moviePosterSize: Int,
                                movieBackdropSize: Int,
                                listType: UserMovieListType): LiveData<PagedList<UserMovieItem>> {
        return createPagingFactory(moviePosterSize, movieBackdropSize, listType)
                .map { mapDomainMovie(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(CoroutineExecutor(this, dispatchers.default()))
                            .build()
                }
    }

    /**
     * Creates an instance of [MPPagingDataSourceFactory] that is used to retrieve new pages of movies
     * every time the user reaches the end of current page.
     *
     *
     * IMPORTANT:
     * The lambda created as parameter of the factory executes it work in a background thread.
     * It does two basic things in the background:
     *  1 - Produces a List of Movies from the [userMovieListInteractor].
     *  2 - Configures the images path of each Movie in the list with the [imagesPathInteractor].
     */
    private fun createPagingFactory(moviePosterSize: Int,
                                    movieBackdropSize: Int,
                                    listType: UserMovieListType): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            val movieListProcessor: (List<Movie>) -> Unit = { movieList ->
                when (movieList.isNotEmpty()) {
                    true -> callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
                    false -> if (page == 1) _navEvents.postValue(GoToUserAccount)
                }
            }

            when (listType) {
                UserMovieListType.FAVORITE_LIST -> userMovieListInteractor.fetchFavoriteMovies(page, movieListProcessor)
                UserMovieListType.RATED_LIST -> userMovieListInteractor.fetchRatedMovies(page, movieListProcessor)
                UserMovieListType.WATCH_LIST -> userMovieListInteractor.fetchWatchlist(page, movieListProcessor)
            }
        }
    }

    /**
     * Asks the interactor to flush any data that might be locally cached and re-fetch the
     * movie list for the current section being shown.
     */
    private fun refreshData() {
        launch {
            withContext(dispatchers.default()) { userMovieListInteractor.refreshUserMoviesData() }
            postLoadingAndInitializePagedList(
                    currentParam.posterSize,
                    currentParam.backdropSize,
                    currentParam.section
            )
        }
    }

    private fun mapDomainMovie(domainMovie: Movie) = with(domainMovie) {
        UserMovieItem(
                movieId = id,
                headerImageUrl = backdrop_path ?: "empty",
                title = title,
                contentImageUrl = poster_path ?: "empty"
        )
    }
}
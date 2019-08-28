package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import javax.inject.Inject

/**
 * ViewModel implementation for the user movies list section. It allows to fetch all types of
 * listing that the user might have.
 */
class UserMovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                 private val userMovieListInteractor: UserMovieListInteractor,
                                                 private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates = MediatorLiveData<UserMovieListViewState>()
    val viewStates: LiveData<UserMovieListViewState> get() = _viewStates

    private val _navEvents = SingleLiveEvent<UserMovieListNavigationEvent>()
    val navEvents: LiveData<UserMovieListNavigationEvent> get() = _navEvents

    private lateinit var dsFactoryCreator: (() -> MPPagingDataSourceFactory<Movie>)

    private val retry: () -> Unit = {
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    init {
        _viewStates.addSource(userMovieListInteractor.userAccountEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = UserMovieListViewState.showNoConnectivityError(retry)
                is UnknownError -> _viewStates.value = UserMovieListViewState.showUnknownError(retry)
                is UserNotLogged -> _navEvents.value = GoToUserAccount
                is UserChangedLanguage -> refreshMovieListData()
            }
        }
    }

    /**
     * Called when the view is initialized with the favorite movies.
     */
    fun onInitWithFavorites(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                userMovieListInteractor.fetchFavoriteMovies(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Called when the view is initialized with the rated movies.
     */
    fun onInitWithRated(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                userMovieListInteractor.fetchRatedMovies(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Called when the view is initialized with the watchlist movies.
     */
    fun onInitWithWatchlist(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = {
            createPagingFactory(posterSize, backdropSize) { page, callback ->
                userMovieListInteractor.fetchWatchlist(page, callback)
            }
        }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
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
    private fun pushLoadingAndInitializePagedList(dataSourceFactoryCreator: () -> MPPagingDataSourceFactory<Movie>) {
        with(_viewStates) {
            value = UserMovieListViewState.showLoading()
            addSource(createPagedList(dataSourceFactoryCreator)) { pagedList -> value = UserMovieListViewState.showMovieList(pagedList) }
        }
    }

    private fun refreshMovieListData() {
        userMovieListInteractor.refreshUserMoviesData()
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Creates a [LiveData] object of the [PagedList] that is used to wire up the Android Paging Library
     * with the interactor in order to fetch a new page of movies each time the user scrolls down in
     * the list of movies.
     * [dataSourceFactoryCreator] is a factory method that provides a mechanism used to instantiate
     * the proper [MPPagingDataSourceFactory] instance based on the movies fetching strategy required
     * for the section being shown to the user. Check the documentation in [createPagingFactory] in
     * order to fully understand how this behaves.
     */
    private fun createPagedList(dataSourceFactoryCreator: () -> MPPagingDataSourceFactory<Movie>): LiveData<PagedList<UserMovieItem>> {
        return dataSourceFactoryCreator()
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
     * every time the user reaches the end of current page being used. It is basically a method to
     * support the usage of the Android Paging Library.
     * [fetchStrategy] provides a mechanism to execute the proper method in the [UserMovieListInteractor]
     * to fetch the movies for the section being shown, since this VM supports 4 different types of
     * movie sections (Playing, Popular, TopRated and Upcoming).
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
                                    fetchStrategy: (Int, (List<Movie>) -> Unit) -> Unit): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            fetchStrategy(page) { movieList ->
                when (movieList.isNotEmpty()) {
                    true -> callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
                    false -> if (page == 1) _navEvents.postValue(GoToUserAccount)
                }
            }
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
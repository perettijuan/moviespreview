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
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.*
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToUserAccount
import com.jpp.mpaccount.account.lists.UserMovieListViewState.*
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import javax.inject.Inject

//TODO JPP once the movie details is implemented with favorites functionallity, we need to check what happens here with an empty list.
/**
 * ViewModel implementation for the user movies list section. It allows to fetch all types of
 * listing that the user might have.
 */
class UserMovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                 private val userMovieListInteractor: UserMovieListInteractor,
                                                 private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserMovieListViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<UserMovieListNavigationEvent>() }
    private lateinit var dsFactoryCreator: (() -> MPPagingDataSourceFactory<Movie>)

    init {
        _viewStates.addSource(userMovieListInteractor.userAccountEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UnknownError -> _viewStates.value = of(ShowError)
                is UserNotLogged -> _navEvents.value = GoToUserAccount
                is UserChangedLanguage -> refreshMovieListData()
            }
        }
    }

    /**
     * Called when the view is initialized with the favorite movies.
     */
    fun onInitWithFavorites(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = { createFavoritesPagingFactory(posterSize, backdropSize) }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Called when the view is initialized with the rated movies.
     */
    fun onInitWithRated(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = { createRatedPagingFactory(posterSize, backdropSize) }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Called when the view is initialized with the watchlist movies.
     */
    fun onInitWithWatchlist(posterSize: Int, backdropSize: Int) {
        dsFactoryCreator = { createWatchlistPagingFactory(posterSize, backdropSize) }
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Called when the user retries after an error.
     */
    fun onRetry() {
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<UserMovieListViewState>> get() = _viewStates

    /**
     * Subscribe to this [LiveData] in order to get notified about navigation steps that
     * should be performed by the view.
     */
    val navEvents: LiveData<UserMovieListNavigationEvent> get() = _navEvents

    /**
     * Pushes the Loading view state into the view layer and creates the [PagedList]
     * of [UserMovieItem] that will be rendered by the view layer.
     */
    private fun pushLoadingAndInitializePagedList(dataSourceFactoryCreator: () -> MPPagingDataSourceFactory<Movie>) {
        with(_viewStates) {
            value = of(ShowLoading)
            addSource(createPagedList(dataSourceFactoryCreator)) { pagedList -> value = of(ShowMovieList(pagedList)) }
        }
    }

    private fun refreshMovieListData() {
        userMovieListInteractor.refreshUserMoviesData()
        pushLoadingAndInitializePagedList(dsFactoryCreator)
    }

    /**
     * Creates the [LiveData] of [PagedList] that will be pushed to the view layer to render each movie
     * as a [UserMovieItem].
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
     * Creates a [MPPagingDataSourceFactory] instance that will be used to show the list of movies.
     *
     * IMPORTANT:
     * The lambda created as parameter of the factory executes it work in a background thread.
     * It does two basic things in the background:
     *  1 - Produces a List of Movies from the [userMovieListInteractor].
     *  2 - Configures the images path of each Movie in the list with the [imagesPathInteractor].
     */
    private fun createFavoritesPagingFactory(moviePosterSize: Int, movieBackdropSize: Int): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            userMovieListInteractor.fetchFavoriteMovies(page) { movieList ->
                callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
            }
        }
    }

    private fun createRatedPagingFactory(moviePosterSize: Int, movieBackdropSize: Int): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            userMovieListInteractor.fetchRatedMovies(page) { movieList ->
                callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
            }
        }
    }

    private fun createWatchlistPagingFactory(moviePosterSize: Int, movieBackdropSize: Int): MPPagingDataSourceFactory<Movie> {
        return MPPagingDataSourceFactory { page, callback ->
            userMovieListInteractor.fetchWatchlist(page) { movieList ->
                callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
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
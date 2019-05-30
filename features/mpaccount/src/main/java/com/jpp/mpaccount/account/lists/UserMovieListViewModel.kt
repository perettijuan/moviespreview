package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.*
import com.jpp.mpaccount.account.lists.UserMovieListViewState.*
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpaccount.account.lists.UserMovieListNavigationEvent.GoToUserAccount

//TODO JPP once the movie details is implemented with favorites functionallity, we need to check what happens here with an empty list.
//TODO we need to have rated movies and watchlist movies
//TODO JPP add tests
class UserMovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                 private val userMovieListInteractor: UserMovieListInteractor,
                                                 private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserMovieListViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<UserMovieListNavigationEvent>() }

    init {
        _viewStates.addSource(userMovieListInteractor.userAccountEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UnknownError -> _viewStates.value = of(ShowError)
                is UserNotLogged -> _navEvents.value = GoToUserAccount
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(posterSize: Int, backdropSize: Int) {
        pushLoadingAndInitializePagedList(posterSize, backdropSize)
    }

    /**
     * Called when the user retries after an error.
     */
    fun onRetry(posterSize: Int, backdropSize: Int) {
        pushLoadingAndInitializePagedList(posterSize, backdropSize)
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
    private fun pushLoadingAndInitializePagedList(posterSize: Int, backdropSize: Int) {
        with(_viewStates) {
            value = of(ShowLoading)
            addSource(createPagedList(posterSize, backdropSize)) { pagedList -> value = of(ShowMovieList(pagedList)) }
        }
    }

    /**
     * Creates the [LiveData] of [PagedList] that will be pushed to the view layer to render each movie
     * as a [UserMovieItem].
     */
    private fun createPagedList(moviePosterSize: Int, movieBackdropSize: Int): LiveData<PagedList<UserMovieItem>> {
        return MPPagingDataSourceFactory<Movie> { page, callback -> fetchMoviePageAsync(page, moviePosterSize, movieBackdropSize, callback) }
                .map { mapDomainMovie(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .build()
                }
    }

    /**
     * This is the actual function that retrieves the movie page. It executes it work in a background thread (the default
     * dispatcher used by the coroutine).
     * It does two basic things in the background:
     *  1 - Produces a List of Movies from the [userMovieListInteractor].
     *  2 - Configures the images path of each Movie in the list with the [imagesPathInteractor].
     */
    private fun fetchMoviePageAsync(page: Int, moviePosterSize: Int, movieBackdropSize: Int, callback: (List<Movie>) -> Unit) {
        launch {
            withContext(dispatchers.default()) {
                userMovieListInteractor.fetchFavoriteMovies(page) { movieList ->
                    callback(movieList.map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) })
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
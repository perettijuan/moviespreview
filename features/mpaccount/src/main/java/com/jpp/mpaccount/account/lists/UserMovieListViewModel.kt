package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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

//TODO JPP we have to see what happens with the empty list
class UserMovieListViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                 private val userMovieListInteractor: UserMovieListInteractor,
                                                 private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserMovieListViewState>>() }

    init {
        _viewStates.addSource(userMovieListInteractor.userAccountEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UnknownError -> _viewStates.value = of(ShowError)
                is UserNotLogged -> TODO("TODO JPP")
            }
        }
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<UserMovieListViewState>> get() = _viewStates

    fun onInit(posterSize: Int, backdropSize: Int) {
        initializePagedList(posterSize, backdropSize)
    }

    /**
     * Attempts to execute the last movie fetching step that was executed. Typically called after an error
     * is detected.
     */
    fun onRetry(posterSize: Int, backdropSize: Int) {
        initializePagedList(posterSize, backdropSize)
    }


    private fun initializePagedList(posterSize: Int, backdropSize: Int) {
        with(_viewStates) {
            value = of(ShowLoading)
            addSource(createPagedList(posterSize, backdropSize)) { pagedList -> value = of(ShowMovieList(pagedList)) }
        }
    }

    private fun createPagedList(moviePosterSize: Int,
                                movieBackdropSize: Int): LiveData<PagedList<UserMovieItem>> {
        return MPPagingDataSourceFactory<Movie> { page, callback -> fetchMoviePageAsync(page, callback) }
                .map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) }
                .map { mapDomainMovie(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .build()
                }
    }

    private fun fetchMoviePageAsync(page: Int, callback: (List<Movie>) -> Unit) {
        launch { withContext(dispatchers.default()) { userMovieListInteractor.fetchFavoriteMovies(page, callback) } }
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
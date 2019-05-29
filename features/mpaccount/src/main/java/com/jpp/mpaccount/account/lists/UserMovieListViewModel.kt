package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.*
import com.jpp.mpaccount.account.lists.UserMovieListViewState.*
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import java.util.concurrent.Executor
import javax.inject.Inject

class UserMovieListViewModel @Inject constructor(private val userMovieListInteractor: UserMovieListInteractor,
                                                 private val imagesPathInteractor: ImagesPathInteractor,
                                                 private val networkExecutor: Executor)
    : ViewModel() {


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

    fun onInit(posterSize: Int,
               backdropSize: Int) {
        _viewStates.value = of(ShowLoading)

        _viewStates.addSource(createPagedList(posterSize, backdropSize)) { pagedList ->
            when (pagedList.isEmpty()) {
                true -> of(ShowEmptyList)
                false -> of(ShowMovieList(pagedList))
            }.let { _viewStates.value = it }
        }
    }


    private fun createPagedList(moviePosterSize: Int,
                                movieBackdropSize: Int): LiveData<PagedList<UserMovieItem>> {
        return MPPagingDataSourceFactory<Movie> { page, callback -> userMovieListInteractor.fetchFavoriteMovies(page, callback) }
                //TODO JPP .apply { retryFunc = { networkExecutor.execute { retryLast() } } }
                .map { imagesPathInteractor.configurePathMovie(moviePosterSize, movieBackdropSize, it) }
                .map { mapDomainMovie(it) }
                .let {
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(2)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
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
package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.common.paging.MPPagingDataSourceFactory
import com.jpp.mpaccount.account.lists.UserMovieListInteractor.UserMovieListEvent.*
import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPViewModel] used to support the user's movie list section of the application.
 * Produces different [UserMovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class UserMovieListViewModel @Inject constructor(
    private val getMoviesUseCase: GetUserAccountMoviePageUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : MPViewModel() {

    private val _viewState = MutableLiveData<UserMovieListViewState>()
    internal val viewState: LiveData<UserMovieListViewState> = _viewState

    private var currentPage: Int = FIRST_PAGE
    private lateinit var currentParam: UserMovieListParam

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(param: UserMovieListParam) {
        currentPage = FIRST_PAGE
        currentParam = param
        updateCurrentDestination(Destination.ReachedDestination(param.screenTitle))
        _viewState.value = UserMovieListViewState.showLoading()
        fetchMoviePage(currentPage, param.section)
    }

    /**
     * Called when a new page in the search is needed.
     */
    internal fun onNextPageRequested() {
        val nextPage = currentPage + 1
        fetchMoviePage(nextPage, currentParam.section)
    }

    /**
     * Called when an item is selected in the list of movies.
     * A new state is posted in navEvents() in order to handle the event.
     */
    internal fun onMovieSelected(movieItem: UserMovieItem) {
        with(movieItem) {
            navigateTo(
                Destination.MPMovieDetails(
                    movieId = movieId.toString(),
                    movieImageUrl = contentImageUrl,
                    movieTitle = title
                )
            )
        }
    }

    private fun fetchMoviePage(
        page: Int,
        listType: UserMovieListType
    ) {
        viewModelScope.launch {
            val accountMovieType = when (listType) {
                UserMovieListType.FAVORITE_LIST -> AccountMovieType.Favorite
                UserMovieListType.RATED_LIST -> AccountMovieType.Rated
                UserMovieListType.WATCH_LIST -> AccountMovieType.Watchlist
            }

            val result = withContext(ioDispatcher) {
                getMoviesUseCase.execute(page, accountMovieType)
            }

            when (result) {
                is Try.Success -> processMoviePage(result.value)
                is Try.Failure -> processFailureCause(result.cause)
            }
        }
    }

    private fun processFailureCause(cause: Try.FailureCause) {
        when (cause) {
            is Try.FailureCause.NoConnectivity -> _viewState.value =
                _viewState.value?.showNoConnectivityError {
                    fetchMoviePage(currentPage, currentParam.section)
                }
            is Try.FailureCause.Unknown -> _viewState.value =
                _viewState.value?.showUnknownError {
                    fetchMoviePage(currentPage, currentParam.section)
                }
            is Try.FailureCause.UserNotLogged -> navigateTo(Destination.PreviousDestination)
        }
    }

    private fun processMoviePage(moviePage: MoviePage) {
        currentPage = moviePage.page
        _viewState.value =
            _viewState.value?.showMovieList(moviePage.results.map { movie -> movie.mapToMovieItem() })
    }

    private fun Movie.mapToMovieItem(): UserMovieItem = UserMovieItem(
        movieId = id,
        headerImageUrl = backdrop_path ?: "empty",
        title = title,
        contentImageUrl = poster_path ?: "empty"
    )

    private companion object {
        const val FIRST_PAGE = 1
    }
}

package com.jpp.mpaccount.account.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] used to support the user's movie list section of the application.
 * Produces different [UserMovieListViewState] that represents the entire configuration of the screen at any
 * given moment.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class UserMovieListViewModel(
    private val getMoviesUseCase: GetUserAccountMoviePageUseCase,
    private val navigator: UserMovieListNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewState = MutableLiveData<UserMovieListViewState>()
    internal val viewState: LiveData<UserMovieListViewState> = _viewState

    private var currentPage: Int
        set(value) = savedStateHandle.set(CURRENT_PAGE_KEY, value)
        get() = savedStateHandle.get(CURRENT_PAGE_KEY) ?: FIRST_PAGE

    private var listType: UserMovieListType
        set(value) = savedStateHandle.set(MOVIE_LIST_TYPE, value)
        get() = savedStateHandle.get(MOVIE_LIST_TYPE)
            ?: throw IllegalStateException("list type not initialized yet")
    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(listType: UserMovieListType) {
        currentPage = FIRST_PAGE
        this.listType = listType
        _viewState.value = UserMovieListViewState.showLoading(listType.titleRes)
        fetchMoviePage(currentPage, listType)
    }

    /**
     * Called when a new page in the search is needed.
     */
    internal fun onNextPageRequested() {
        val nextPage = currentPage + 1
        fetchMoviePage(nextPage, listType)
    }

    /**
     * Called when an item is selected in the list of movies.
     * A new state is posted in navEvents() in order to handle the event.
     */
    internal fun onMovieSelected(movieItem: UserMovieItem) {
        navigator.navigateToMovieDetails(
            movieId = movieItem.movieId.toString(),
            movieImageUrl = movieItem.contentImageUrl,
            movieTitle = movieItem.title
        )
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
                    fetchMoviePage(currentPage, listType)
                }
            is Try.FailureCause.Unknown -> _viewState.value =
                _viewState.value?.showUnknownError {
                    fetchMoviePage(currentPage, listType)
                }
            is Try.FailureCause.UserNotLogged -> navigator.navigateHome()
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
        const val CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY"
        const val MOVIE_LIST_TYPE = "MOVIE_LIST_TYPE"
        const val FIRST_PAGE = 1
    }
}

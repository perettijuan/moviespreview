package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.usecase.GetUserAccountMoviesUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LogOutUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] to handle the state of the [UserAccountFragment].
 * It consumes data coming from the lower layers
 * and maps that data to view logic.
 */
class UserAccountViewModel(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val getMoviesUseCase: GetUserAccountMoviesUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val navigator: UserAccountNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _headerViewState = MutableLiveData<UserAccountHeaderState>()
    internal val headerViewState: LiveData<UserAccountHeaderState> = _headerViewState

    private val _bodyViewState = MutableLiveData<UserAccountBodyViewState>()
    internal val bodyViewState: LiveData<UserAccountBodyViewState> = _bodyViewState

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit() {
        // launch in parallel
        viewModelScope.launch { fetchUserAccountHeader() }
        viewModelScope.launch { fetchUserAccountBody() }
    }

    /**
     * Called when the user wants to perform the logout of the application.
     */
    internal fun onLogout() {
        _headerViewState.value = UserAccountHeaderState.showLoading()
        _bodyViewState.value = UserAccountBodyViewState.showLoading()
        viewModelScope.launch {
            logOutUseCase.execute() // result is not important
            navigator.navigateHome()
        }
    }

    /**
     * Called when the user wants to see the favorites movies.
     */
    internal fun onFavorites() {
        navigator.navigateToFavorites()
    }

    /**
     * Called when the user wants to see the rated movies.
     */
    internal fun onRated() {
        navigator.navigateToRated()
    }

    /**
     * Called when the user wants to see the watchlist.
     */
    internal fun onWatchlist() {
        navigator.navigateToWatchList()
    }

    private suspend fun fetchUserAccountHeader() {
        _headerViewState.value = UserAccountHeaderState.showLoading()
        val result = withContext(ioDispatcher) {
            getUserAccountUseCase.execute()
        }

        when (result) {
            is Try.Success -> processUserAccount(result.value)
            is Try.Failure -> _headerViewState.value = _headerViewState.value?.hide()
        }
    }

    private suspend fun fetchUserAccountBody() {
        _bodyViewState.value = UserAccountBodyViewState.showLoading()

        val moviesResult = withContext(ioDispatcher) {
            getMoviesUseCase.execute(FIRST_PAGE)
        }

        when (moviesResult) {
            is Try.Success -> processMoviesPageResult(moviesResult.value)
            is Try.Failure -> processMoviesPageFailureCause(moviesResult.cause)
        }
    }

    private fun processUserAccount(account: UserAccount) {
        _headerViewState.value = _headerViewState.value?.withAvatar(
            userName = account.getUserName(),
            accountName = account.username,
            avatarUrl = account.avatar.getFullUrl(),
            avatarCallback = { userAvatarCallback(account) }
        )
    }

    private fun userAvatarCallback(account: UserAccount) {
        _headerViewState.value = _headerViewState.value?.withLetter(
            userName = account.getUserName(),
            accountName = account.username,
            defaultLetter = account.getUserLetter()
        )
    }

    private fun processMoviesPageResult(result: Map<AccountMovieType, MoviePage>) {
        val viewStateMapper: (MoviePage?, UserMoviesViewState) -> UserMoviesViewState =
            { page, emptyViewState ->
                when {
                    page == null -> UserMoviesViewState.createError()
                    page.results.isEmpty() -> emptyViewState
                    else -> UserMoviesViewState.createWithItems(page.results.mapToUserMovieItem())
                }
            }

        val favoritesViewState = viewStateMapper(
            result[AccountMovieType.Favorite],
            UserMoviesViewState.createFavoriteEmpty()
        )
        val watchListViewState = viewStateMapper(
            result[AccountMovieType.Watchlist],
            UserMoviesViewState.createWatchlistEmpty()
        )
        val ratedViewState = viewStateMapper(
            result[AccountMovieType.Rated],
            UserMoviesViewState.createRatedEmpty()
        )

        _bodyViewState.value = _bodyViewState.value?.showContentWithMovies(
            favoriteMovieState = favoritesViewState,
            ratedMovieState = ratedViewState,
            watchListState = watchListViewState
        )
    }

    private fun processMoviesPageFailureCause(cause: Try.FailureCause) {
        when (cause) {
            is Try.FailureCause.NoConnectivity -> _bodyViewState.value =
                _bodyViewState.value?.showNoConnectivityError { onInit() }
            is Try.FailureCause.Unknown -> _bodyViewState.value =
                _bodyViewState.value?.showUnknownError { onInit() }
            is Try.FailureCause.UserNotLogged -> navigator.navigateHome()
        }
    }

    private fun List<Movie>.mapToUserMovieItem(): List<UserMovieItem> {
        return toMutableList().map { movie -> UserMovieItem(movie.poster_path ?: "noPath") }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}

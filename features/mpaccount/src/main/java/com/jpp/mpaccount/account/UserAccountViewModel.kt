package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.*
import com.jpp.mpdomain.usecase.GetUserAccountMoviesUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
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
    private val navigator: UserAccountNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState = MediatorLiveData<UserAccountViewState>()
    internal val viewState: LiveData<UserAccountViewState> = _viewState


    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit() {
        _viewState.value = UserAccountViewState.showLoading()
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getUserAccountUseCase.execute()
            }

            when (result) {
                is Try.Success -> processUserAccount(result.value)
                is Try.Failure -> processFailureCause(result.cause)
            }

            val moviesResult = withContext(ioDispatcher) {
                getMoviesUseCase.execute(FIRST_PAGE)
            }

            when (moviesResult) {
                is Try.Success -> processMoviesPageResult(moviesResult.value)
                is Try.Failure -> processMoviesPageFailureCause(moviesResult.cause)
            }
        }
    }

    /**
     * Called when the user wants to perform the logout of the application.
     */
    internal fun onLogout() {
        TODO("Implement ME")
        //withAccountInteractor { clearUserAccountData() }
        _viewState.value = UserAccountViewState.showLoading()
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

    private fun processUserAccount(account: UserAccount) {
        _viewState.value = _viewState.value?.showAccountDataWithAvatar(
            userName = account.getUserName(),
            accountName = account.username,
            avatarUrl = account.avatar.getFulUrl(),
            avatarCallback = { userAvatarCallback(account) }
        )
    }

    private fun processFailureCause(cause: Try.FailureCause) {
        when (cause) {
            is Try.FailureCause.NoConnectivity -> _viewState.value =
                _viewState.value?.showNoConnectivityError { onInit() }
            is Try.FailureCause.Unknown -> _viewState.value =
                _viewState.value?.showUnknownError { onInit() }
            is Try.FailureCause.UserNotLogged -> navigator.navigateToLogin()
        }
    }

    private fun userAvatarCallback(account: UserAccount) {
        _viewState.value = _viewState.value?.showAccountDataWithLetter(
            userName = account.getUserName(),
            accountName = account.username,
            defaultLetter = account.getUserLetter()
        )
    }

    private fun processMoviesPageResult(result: Map<AccountMovieType, MoviePage>) {
        val viewStateMapper: (MoviePage?) -> UserMoviesViewState =
            { page ->
                when {
                    page == null -> UserMoviesViewState.createError()
                    page.results.isEmpty() -> UserMoviesViewState.createFavoriteEmpty() //TODO JPP handle this case
                    else -> UserMoviesViewState.createWithItems(page.results.mapToUserMovieItem())
                }
            }

        val favoritesViewState = viewStateMapper(result[AccountMovieType.Favorite])
        val watchListViewState = viewStateMapper(result[AccountMovieType.Watchlist])
        val ratedViewState = viewStateMapper(result[AccountMovieType.Rated])

        _viewState.value = _viewState.value?.showAccountMovies(
            favoritesViewState,
            watchListViewState,
            ratedViewState
        )
    }

    private fun processMoviesPageFailureCause(cause: Try.FailureCause) {
        TODO("This is next")
    }


    private fun UserAccount.getUserName(): String = if (name.isEmpty()) username else name
    private fun UserAccount.getUserLetter(): String =
        if (name.isEmpty()) username.first().toString() else name.first().toString()

    private fun UserAvatar.getFulUrl(): String =
        Gravatar.BASE_URL + gravatar.hash + Gravatar.REDIRECT

    private fun List<Movie>.mapToUserMovieItem(): List<UserMovieItem> {
        return toMutableList().map { movie -> UserMovieItem(movie.poster_path ?: "noPath") }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}

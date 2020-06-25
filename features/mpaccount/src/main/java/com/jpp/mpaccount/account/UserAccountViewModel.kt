package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.UserAvatar
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
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
    private val getUserAccountMoviePageUseCase: GetUserAccountMoviePageUseCase,
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



        }
    }

    /**
     * Called when the user wants to perform the logout of the application.
     */
    internal fun onLogout() {
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


//    private fun mapAccountInfo(successState: UserAccountEvent.Success) {
//        viewModelScope.launch {
//            with(successState.data) {
//                val favoriteMovieState =
//                    getUserMoviesViewState(successState.favoriteMovies) { UserMoviesViewState.createFavoriteEmpty() }
//                val ratedMovieState =
//                    getUserMoviesViewState(successState.ratedMovies) { UserMoviesViewState.createRatedEmpty() }
//                val watchListState =
//                    getUserMoviesViewState(successState.watchList) { UserMoviesViewState.createWatchlistEmpty() }
//
//                UserAccountViewState.showContentWithAvatar(
//                    userName = if (name.isEmpty()) username else name,
//                    accountName = username,
//                    favoriteMovieState = favoriteMovieState,
//                    ratedMovieState = ratedMovieState,
//                    watchListState = watchListState,
//                    avatarUrl = Gravatar.BASE_URL + avatar.gravatar.hash + Gravatar.REDIRECT
//                ) {
//                    mapAccountInfoWithoutAvatar(
//                        successState.data,
//                        favoriteMovieState,
//                        ratedMovieState,
//                        watchListState
//                    )
//                }
//            }.let { _viewState.value = it }
//        }
//    }

//    /**
//     * Method called when the UI is unable to download the user avatar. The VM
//     * will render a new [UserAccountViewState] that will show the user's name letter
//     * instead of the user's avatar.
//     */
//    private fun mapAccountInfoWithoutAvatar(
//        userAccount: UserAccount,
//        favViewState: UserMoviesViewState,
//        ratedViewState: UserMoviesViewState,
//        watchViewState: UserMoviesViewState
//    ) {
//        _viewState.value = UserAccountViewState.showContentWithLetter(
//            userName = if (userAccount.name.isEmpty()) userAccount.username else userAccount.name,
//            accountName = userAccount.username,
//            favoriteMovieState = favViewState,
//            ratedMovieState = ratedViewState,
//            watchListState = watchViewState,
//            defaultLetter = if (userAccount.name.isEmpty()) userAccount.username.first()
//                .toString() else userAccount.name.first().toString()
//        )
//    }
//
//
//    private suspend fun getUserMoviesViewState(
//        userMovieState: UserMoviesState,
//        emptyCreator: () -> UserMoviesViewState
//    ) = withContext(ioDispatcher) {
//        when (userMovieState) {
//            is UserMoviesState.UnknownError -> UserMoviesViewState.createError()
//            is UserMoviesState.Success -> {
//                when {
//                    userMovieState.data.results.isEmpty() -> emptyCreator()
//                    else -> userMovieState.data.results
//                        .map { imagesPathInteractor.configurePathMovie(posterSize, posterSize, it) }
//                        .map { UserMovieItem(image = it.poster_path ?: "noPath") }
//                        .let { UserMoviesViewState.createWithItems(it) }
//                }
//            }
//        }
//    }

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

    private fun UserAccount.getUserName(): String {
        return if (name.isEmpty()) {
            username
        } else {
            name
        }
    }

    private fun UserAccount.getUserLetter(): String {
        return if (name.isEmpty()) {
            username.first().toString()
        } else {
            name.first().toString()
        }
    }

    private fun UserAvatar.getFulUrl(): String {
        return Gravatar.BASE_URL + gravatar.hash + Gravatar.REDIRECT
    }

}

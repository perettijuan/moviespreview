package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpaccount.account.UserAccountFragmentDirections.userMovieListFragment
import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent
import com.jpp.mpaccount.account.UserAccountInteractor.UserMoviesState
import com.jpp.mpaccount.account.lists.UserMovieListType
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [MPViewModel] to handle the state of the [UserAccountFragment]. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It consumes data coming from the lower layers - exposed by interactors -
 * and maps that data to view logic.
 *
 * This VM consumes data from two interactors - [UserAccountInteractor] and [ImagesPathInteractor] -
 * because it needs to map the URL of the movies to be shown before rendering the data.
 */
class UserAccountViewModel(
    private val accountInteractor: UserAccountInteractor,
    private val imagesPathInteractor: ImagesPathInteractor
) : MPViewModel() {

    private val _viewState = MediatorLiveData<UserAccountViewState>()
    internal val viewState: LiveData<UserAccountViewState> = _viewState

    private val retry: () -> Unit = { onInit(currentParam) }

    private lateinit var currentParam: UserAccountParam

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(accountInteractor.userAccountEvents) { event ->
            when (event) {
                is UserAccountEvent.NotConnectedToNetwork -> _viewState.value = UserAccountViewState.showNoConnectivityError(retry)
                is UserAccountEvent.UnknownError -> _viewState.value = UserAccountViewState.showUnknownError(retry)
                is UserAccountEvent.UserNotLogged -> navigateTo(Destination.PreviousDestination)
                is UserAccountEvent.UserDataCleared -> navigateTo(Destination.PreviousDestination)
                is UserAccountEvent.UserChangedLanguage -> refreshData()
                is UserAccountEvent.Success -> mapAccountInfo(event)
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(param: UserAccountParam) {
        updateCurrentDestination(Destination.ReachedDestination(param.screenTitle))
        currentParam = param
        withAccountInteractor { fetchUserAccountData() }
        _viewState.value = UserAccountViewState.showLoading()
    }

    /**
     * Called when the user wants to perform the logout of the application.
     */
    internal fun onLogout() {
        withAccountInteractor { clearUserAccountData() }
        _viewState.value = UserAccountViewState.showLoading()
    }

    /**
     * Called when the user wants to see the favorites movies.
     */
    internal fun onFavorites() {
        navigateTo(Destination.InnerDestination(userMovieListFragment(UserMovieListType.FAVORITE_LIST)))
    }

    /**
     * Called when the user wants to see the rated movies.
     */
    internal fun onRated() {
        navigateTo(Destination.InnerDestination(userMovieListFragment(UserMovieListType.RATED_LIST)))
    }

    /**
     * Called when the user wants to see the watchlist.
     */
    internal fun onWatchlist() {
        navigateTo(Destination.InnerDestination(userMovieListFragment(UserMovieListType.WATCH_LIST)))
    }

    private fun refreshData() {
        withAccountInteractor { refreshUserAccountData() }
        _viewState.value = UserAccountViewState.showLoading()
    }

    private fun mapAccountInfo(successState: UserAccountEvent.Success) {
        viewModelScope.launch {
            with(successState.data) {
                val favoriteMovieState = getUserMoviesViewState(successState.favoriteMovies) { UserMoviesViewState.createFavoriteEmpty() }
                val ratedMovieState = getUserMoviesViewState(successState.ratedMovies) { UserMoviesViewState.createRatedEmpty() }
                val watchListState = getUserMoviesViewState(successState.watchList) { UserMoviesViewState.createWatchlistEmpty() }

                UserAccountViewState.showContentWithAvatar(
                        userName = if (name.isEmpty()) username else name,
                        accountName = username,
                        favoriteMovieState = favoriteMovieState,
                        ratedMovieState = ratedMovieState,
                        watchListState = watchListState,
                        avatarUrl = Gravatar.BASE_URL + avatar.gravatar.hash + Gravatar.REDIRECT
                ) {
                    mapAccountInfoWithoutAvatar(
                            successState.data,
                            favoriteMovieState,
                            ratedMovieState,
                            watchListState
                    )
                }
            }.let { _viewState.value = it }
        }
    }

    /**
     * Method called when the UI is unable to download the user avatar. The VM
     * will render a new [UserAccountViewState] that will show the user's name letter
     * instead of the user's avatar.
     */
    private fun mapAccountInfoWithoutAvatar(
        userAccount: UserAccount,
        favViewState: UserMoviesViewState,
        ratedViewState: UserMoviesViewState,
        watchViewState: UserMoviesViewState
    ) {
        _viewState.value = UserAccountViewState.showContentWithLetter(
                userName = if (userAccount.name.isEmpty()) userAccount.username else userAccount.name,
                accountName = userAccount.username,
                favoriteMovieState = favViewState,
                ratedMovieState = ratedViewState,
                watchListState = watchViewState,
                defaultLetter = if (userAccount.name.isEmpty()) userAccount.username.first().toString() else userAccount.name.first().toString()
        )
    }

    private fun withAccountInteractor(action: UserAccountInteractor.() -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                action(accountInteractor)
            }
        }
    }

    private suspend fun getUserMoviesViewState(
        userMovieState: UserMoviesState,
        emptyCreator: () -> UserMoviesViewState
    ) = withContext(Dispatchers.IO) {
        when (userMovieState) {
            is UserMoviesState.UnknownError -> UserMoviesViewState.createError()
            is UserMoviesState.Success -> {
                when {
                    userMovieState.data.results.isEmpty() -> emptyCreator()
                    else -> userMovieState.data.results
                            .map { imagesPathInteractor.configurePathMovie(currentParam.posterSize, currentParam.posterSize, it) }
                            .map { UserMovieItem(image = it.poster_path ?: "noPath") }
                            .let { UserMoviesViewState.createWithItems(it) }
                }
            }
        }
    }
}

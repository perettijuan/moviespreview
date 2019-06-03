package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpaccount.account.UserAccountInteractor.UserMoviesState
import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent
import com.jpp.mpaccount.account.UserAccountNavigationEvent.GoToLogin
import com.jpp.mpaccount.account.UserAccountViewState.*
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.jpp.mpaccount.account.UserAccountNavigationEvent.GoToMain
import com.jpp.mpaccount.account.UserAccountNavigationEvent.GoToFavorites
import com.jpp.mpaccount.account.UserAccountNavigationEvent.GoToRated
import com.jpp.mpaccount.account.UserAccountNavigationEvent.GoToWatchlist
import javax.inject.Inject

/**
 * [MPScopedViewModel] to handle the state of the [UserAccountFragment]. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It consumes data coming from the lower layers - exposed by interactors -
 * and maps that data to view logic.
 *
 * This VM consumes data from two interactors - [UserAccountInteractor] and [ImagesPathInteractor] -
 * because it needs to map the URL of the movies to be shown before rendering the data.
 */
class UserAccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                               private val accountInteractor: UserAccountInteractor,
                                               private val imagesPathInteractor: ImagesPathInteractor)

    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserAccountViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<UserAccountNavigationEvent>() }
    private var moviesPosterTargetSize: Int = 0

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(accountInteractor.userAccountEvents) { event ->
            when (event) {
                is UserAccountEvent.NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UserAccountEvent.UnknownError -> _viewStates.value = of(ShowError)
                is UserAccountEvent.UserNotLogged -> _navEvents.value = GoToLogin
                is UserAccountEvent.UserDataCleared -> _navEvents.value = GoToMain
                is UserAccountEvent.UserChangedLanguage -> _viewStates.value = of(refreshData())
                is UserAccountEvent.Success -> mapAccountInfo(event)
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(posterSize: Int) {
        moviesPosterTargetSize = posterSize
        _viewStates.value = of(executeGetUserAccountStep())
    }

    /**
     * Called when the user retries after an error.
     */
    fun onUserRetry(posterSize: Int) {
        moviesPosterTargetSize = posterSize
        _viewStates.value = of(executeGetUserAccountStep())
    }

    /**
     * Called when the user wants to perform the logout of the application.
     */
    fun onLogout() {
        _viewStates.value = of(executeLogout())
    }

    /**
     * Called when the user wants to see the favorites movies.
     */
    fun onFavorites() {
        _navEvents.value = GoToFavorites
    }

    /**
     * Called when the user wants to see the rated movies.
     */
    fun onRated() {
        _navEvents.value = GoToRated
    }

    /**
     * Called when the user wants to see the watchlist.
     */
    fun onWatchlist() {
        _navEvents.value = GoToWatchlist
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<UserAccountViewState>> get() = _viewStates

    /**
     * Subscribe to this [LiveData] in order to get notified about navigation steps that
     * should be performed by the view.
     */
    val navEvents: LiveData<UserAccountNavigationEvent> get() = _navEvents


    private fun executeGetUserAccountStep(): UserAccountViewState {
        withAccountInteractor { fetchUserAccountData() }
        return ShowLoading
    }

    private fun executeLogout(): UserAccountViewState {
        withAccountInteractor { clearUserAccountData() }
        return ShowLoading
    }

    private fun refreshData() : UserAccountViewState {
        withAccountInteractor { refreshUserAccountData() }
        return ShowLoading
    }

    private fun mapAccountInfo(successState: UserAccountEvent.Success) {
        launch {
            with(successState.data) {
                ShowUserAccountData(
                        avatarUrl = Gravatar.BASE_URL + avatar.gravatar.hash + Gravatar.REDIRECT,
                        userName = if (name.isEmpty()) username else name,
                        accountName = username,
                        defaultLetter = if (name.isEmpty()) username.first() else name.first(),
                        favoriteMovieState = getUserMoviesViewState(successState.favoriteMovies),
                        ratedMovieState = getUserMoviesViewState(successState.ratedMovies),
                        watchListState = getUserMoviesViewState(successState.watchList)
                )
            }.let { _viewStates.value = of(it) }
        }
    }


    private fun withAccountInteractor(action: UserAccountInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(accountInteractor) } }
    }

    private suspend fun getUserMoviesViewState(userMovieState: UserMoviesState) = withContext(dispatchers.default()) {
        when (userMovieState) {
            is UserMoviesState.UnknownError -> UserMoviesViewState.ShowError
            is UserMoviesState.Success -> {
                when {
                    userMovieState.data.results.isEmpty() -> UserMoviesViewState.ShowNoMovies
                    else -> userMovieState.data.results
                            .map { imagesPathInteractor.configurePathMovie(moviesPosterTargetSize, moviesPosterTargetSize, it) }
                            .map { UserMovieItem(image = it.poster_path ?: "noPath") }
                            .let { UserMoviesViewState.ShowUserMovies(it) }
                }
            }
        }
    }
}
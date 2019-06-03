package com.jpp.mp.screens.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.UserAccount
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.jpp.mp.screens.main.header.HeaderViewState.ShowLoading
import com.jpp.mp.screens.main.header.HeaderViewState.ShowLogin
import com.jpp.mp.screens.main.header.HeaderViewState.ShowAccount
import com.jpp.mp.screens.main.header.NavigationHeaderInteractor.HeaderDataEvent.*
import com.jpp.mpdomain.Gravatar

/**
 * [MPScopedViewModel] to handle the state of the [NavigationHeaderFragment]. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It consumes data coming from the lower layers - exposed by [NavigationHeaderInteractor] -
 * and maps that data to view logic.
 */
class NavigationHeaderViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                    private val interactor: NavigationHeaderInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<HeaderViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<HeaderNavigationEvent>() }


    init {
        _viewStates.addSource(interactor.userAccountEvents) { event ->
            when (event) {
                is UserNotLogged -> _viewStates.value = of(ShowLogin)
                is Success -> _viewStates.value = of(mapAccountInfo(event.data))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit() {
        _viewStates.value = of(getAccountInfo())
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<HeaderViewState>> get() = _viewStates

    /**
     * Subscribe to this [LiveData] in order to get notified about navigation steps that
     * should be performed by the view.
     */
    val navEvents: LiveData<HeaderNavigationEvent> get() = _navEvents

    /**
     * Called when the user attempts to navigate to the login screen.
     */
    fun onUserNavigatesToLogin() {
        _navEvents.value = HeaderNavigationEvent.ToLogin
    }

    /**
     * Called when the user attempts to navigate to the account details.
     */
    fun onUserNavigatesToAccountDetails() {
        _navEvents.value = HeaderNavigationEvent.ToUserAccount
    }

    /**
     * Executes the use case to fetch the user account info. If an error is detected, the
     * login view will be shown in order to let the user attempt a login.
     */
    private fun getAccountInfo(): HeaderViewState {
        launch { withContext(dispatchers.default()) { interactor.fetchUserData() } }
        return ShowLoading
    }

    private fun mapAccountInfo(userAccount: UserAccount): HeaderViewState = with(userAccount) {
        ShowAccount(
                avatarUrl = Gravatar.BASE_URL + avatar.gravatar.hash + Gravatar.REDIRECT,
                userName = if (name.isEmpty()) username else name,
                defaultLetter = if (name.isEmpty()) username.first() else name.first(),
                accountName = username
        )
    }
}
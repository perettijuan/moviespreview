package com.jpp.mp.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.main.header.NavigationHeaderInteractor.HeaderDataEvent.*
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the [NavigationHeaderFragment] behavior. It retrieves the
 * user account data and state using the provided [NavigationHeaderInteractor] and updates the view
 * state that the Fragment takes care of rendering.
 */
class NavigationHeaderViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                    private val interactor: NavigationHeaderInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HeaderViewState>() }
    val viewStates: LiveData<HeaderViewState> get() = _viewStates

    private val _navEvents by lazy { SingleLiveEvent<HeaderNavigationEvent>() }
    val navEvents: LiveData<HeaderNavigationEvent> get() = _navEvents

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(interactor.userAccountEvents) { event ->
            when (event) {
                is UserNotLogged -> _viewStates.value = HeaderViewState.showLogin()
                is UnknownError -> _viewStates.value = HeaderViewState.showLogin()
                is Success -> _viewStates.value = mapAccountInfo(event.data)
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit() {
        launch { withContext(dispatchers.default()) { interactor.getUserAccountData() } }
        _viewStates.value = HeaderViewState.showLoading()
    }

    /**
     * Called when the the navigate to login option is selected in the UI.
     * A new state is posted in [navEvents] in order to handle the event.
     */
    fun onNavigateToLoginSelected() {
        _navEvents.value = HeaderNavigationEvent.ToLogin
    }

    /**
     * Called when the user attempts to navigate to the account details.
     * A new state is posted in [navEvents] in order to handle the event.
     */
    fun onNavigateToAccountDetailsSelected() {
        _navEvents.value = HeaderNavigationEvent.ToUserAccount
    }

    /**
     * Map the [UserAccount] obtained into a [HeaderViewState] to be rendered.
     */
    private fun mapAccountInfo(userAccount: UserAccount): HeaderViewState = with(userAccount) {
        HeaderViewState.showAccountWithAvatar(
                avatarUrl = Gravatar.BASE_URL + avatar.gravatar.hash + Gravatar.REDIRECT,
                userName = if (name.isEmpty()) username else name,
                accountName = username,
                avatarCallback = { mapAccountInfoWithoutAvatar(userAccount) }
        )
    }

    /**
     * Method called when the UI is unable to download the user avatar. The VM
     * will render a new [HeaderViewState] that will show the user's name letter
     * instead of the user's avatar.
     */
    private fun mapAccountInfoWithoutAvatar(userAccount: UserAccount) {
        _viewStates.value = HeaderViewState.showAccountWithLetter(
                userName = if (userAccount.name.isEmpty()) userAccount.username else userAccount.name,
                accountName = userAccount.username,
                defaultLetter = (if (userAccount.name.isEmpty()) userAccount.username.first().toString() else userAccount.name.first().toString()).toUpperCase()
        )
    }
}
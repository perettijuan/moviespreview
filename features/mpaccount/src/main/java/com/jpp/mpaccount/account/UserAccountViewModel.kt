package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent
import com.jpp.mpaccount.account.UserAccountNavigationEvent.GoToLogin
import com.jpp.mpaccount.account.UserAccountViewState.*
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.UserAccount
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel that supports the user account functionality.
 */
class UserAccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                               private val accountInteractor: UserAccountInteractor)

    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserAccountViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<UserAccountNavigationEvent>() }

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(accountInteractor.userAccountEvents) { event ->
            when (event) {
                is UserAccountEvent.NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UserAccountEvent.UnknownError -> _viewStates.value = of(ShowError)
                is UserAccountEvent.UserNotLogged -> _navEvents.value = GoToLogin
                is UserAccountEvent.Success -> _viewStates.value = of(mapAccountInfo(event.data))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit() {
        _viewStates.postValue(of(executeGetUserAccountStep()))
    }

    /**
     * Called when the user retries after an error.
     */
    fun onUserRetry() {
        _viewStates.postValue(of(executeGetUserAccountStep()))
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
        launch { withContext(dispatchers.default()) { accountInteractor.fetchUserAccountData() } }
        return Loading
    }

    private fun mapAccountInfo(userAccount: UserAccount) = with(userAccount) {
        ShowUserAccountData(
                avatarUrl = Gravatar.BASE_URL + userAccount.avatar.gravatar.hash + Gravatar.REDIRECT,
                userName = if (name.isEmpty()) username else name,
                accountName = username,
                defaultLetter = if (name.isEmpty()) username.first() else name.first()
        )
    }
}
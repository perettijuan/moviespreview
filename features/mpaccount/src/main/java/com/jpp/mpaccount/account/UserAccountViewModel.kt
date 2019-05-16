package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.repository.MPConnectivityRepository
import com.jpp.mpdomain.repository.MPSessionRepository
import javax.inject.Inject
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData.*
import com.jpp.mpaccount.account.UserAccountNavigationEvent.*
import com.jpp.mpaccount.account.UserAccountViewState.*
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.MPUserAccountRepository
import com.jpp.mpdomain.repository.MPUserAccountRepository.UserAccountData.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                               connectivityRepository: MPConnectivityRepository,
                                               private val sessionRepository: MPSessionRepository,
                                               private val userAccountRepository: MPUserAccountRepository)

    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserAccountViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<UserAccountNavigationEvent>() }

    init {
        _viewStates.addSource(sessionRepository.data()) { sessionData ->
            when (sessionData) {
                is NoCurrentSessionAvailable -> _navEvents.value = GoToLogin
                is CurrentSession -> _viewStates.value = of(executeGetUserAccountStep(sessionData.data))
            }
        }

        _viewStates.addSource(userAccountRepository.data()) { accountData ->
            when (accountData) {
                is Success -> _viewStates.value = of(mapAccountInfo(accountData.data))
                is NoUserAccountData -> _viewStates.value = of(ShowError)
            }
        }

        _viewStates.addSource(connectivityRepository.data()) { connectivity ->
            when (connectivity) {
                is Connectivity.Disconnected -> _viewStates.value = of(NotConnected)
                is Connectivity.Connected ->  _viewStates.value = of(executeVerifyUserLoggedInStep())
            }
        }
    }

    fun onInit() {
        _viewStates.value = of(executeVerifyUserLoggedInStep())
    }

    fun onUserRetry() {
        _viewStates.value = of(executeVerifyUserLoggedInStep())
    }

    val viewStates: LiveData<HandledViewState<UserAccountViewState>> get() = _viewStates
    val navEvents: LiveData<UserAccountNavigationEvent> get() = _navEvents

    private suspend fun verifyUserLoggedIn() = withContext(dispatchers.default()) { sessionRepository.getCurrentSession() }
    private suspend fun getUserAccount(session: Session) = withContext(dispatchers.default()) { userAccountRepository.getUserAccount(session) }

    private fun executeVerifyUserLoggedInStep(): UserAccountViewState {
        launch { verifyUserLoggedIn() }
        return Loading
    }

    private fun executeGetUserAccountStep(session: Session): UserAccountViewState {
        launch { getUserAccount(session) }
        return Loading
    }

    private fun mapAccountInfo(userAccount: UserAccount) = with(userAccount) {
        ShowUserAccountData(
                avatarUrl = Gravatar.BASE_URL + userAccount.avatar.gravatar.hash  + Gravatar.REDIRECT,
                userName = if (name.isEmpty()) username else name,
                accountName = username,
                defaultLetter = if (name.isEmpty()) username.first() else name.first()
        )
    }
}
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserAccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                               connectivityRepository: MPConnectivityRepository,
                                               private val sessionRepository: MPSessionRepository)

    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<UserAccountViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<UserAccountNavigationEvent>() }

    init {
        _viewStates.addSource(sessionRepository.data()) { sessionData ->
            when (sessionData) {
                is NoCurrentSessionAvailable -> _navEvents.value = GoToLogin
            }
        }

        _viewStates.addSource(connectivityRepository.data()) { connectivity ->
            when (connectivity) {
                is Connectivity.Disconnected -> { _viewStates.value = of(NotConnected) }
                is Connectivity.Connected -> { TODO() }
            }
        }
    }

    fun onInit() {
        _viewStates.value = of(executeVerifyUserLoggedInStep())
    }

    val viewStates: LiveData<HandledViewState<UserAccountViewState>> get() = _viewStates
    val navEvents: LiveData<UserAccountNavigationEvent> get() = _navEvents

    private suspend fun verifyUserLoggedIn() = withContext(dispatchers.default()) { sessionRepository.getCurrentSession() }

    private fun executeVerifyUserLoggedInStep(): UserAccountViewState {
        launch { verifyUserLoggedIn() }
        return Loading
    }
}
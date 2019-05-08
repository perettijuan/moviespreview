package com.jpp.mpaccount.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.repository.MPAccessTokenRepository
import com.jpp.mpdomain.repository.MPAccessTokenRepository.AccessTokenData.Success
import com.jpp.mpdomain.repository.MPAccessTokenRepository.AccessTokenData.NoAccessTokenAvailable
import com.jpp.mpdomain.repository.MPSessionRepository
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData.CurrentSession
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData.NoCurrentSessionAvailable
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData.SessionCreated
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData.UnableToCreateSession
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//TODO JPP connectivity!
class LoginViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                         private val sessionRepository: MPSessionRepository,
                                         private val accessTokenRepository: MPAccessTokenRepository)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<LoginViewState>() }
    private val _navEvents by lazy { SingleLiveEvent<LoginNavigationEvent>() }

    init {
        _viewStates.addSource(sessionRepository.data()) { sessionData ->
            when (sessionData) {
                is CurrentSession -> { _navEvents.value = LoginNavigationEvent.BackToPrevious }
                is NoCurrentSessionAvailable -> {
                    _viewStates.value = LoginViewState.Loading
                    launch { getAccessToken() }
                }
                is SessionCreated -> { _navEvents.value = LoginNavigationEvent.BackToPrevious }
                is UnableToCreateSession -> { /* Show error */ }
            }
        }

        _viewStates.addSource(accessTokenRepository.data()) { atData ->
            when (atData) {
                is Success -> Log.d("SACACHISPAS", "AT ${atData.data.request_token}")
                is NoAccessTokenAvailable -> { _viewStates.value = LoginViewState.UnableToLogin }
            }
        }
    }

    fun initialize() {
        launch { verifyUserLoggedIn() }
    }

    val viewStates: LiveData<LoginViewState> get() = _viewStates
    val navEvents: LiveData<LoginNavigationEvent> get() = _navEvents

    private suspend fun verifyUserLoggedIn() = withContext(dispatchers.default()) { sessionRepository.getCurrentSession() }
    private suspend fun getAccessToken() = withContext(dispatchers.default()) { accessTokenRepository.getAccessToken() }

}
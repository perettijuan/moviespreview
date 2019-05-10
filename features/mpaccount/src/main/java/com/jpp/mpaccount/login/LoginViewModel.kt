package com.jpp.mpaccount.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.repository.MPAccessTokenRepository
import com.jpp.mpdomain.repository.MPAccessTokenRepository.AccessTokenData.NoAccessTokenAvailable
import com.jpp.mpdomain.repository.MPAccessTokenRepository.AccessTokenData.Success
import com.jpp.mpdomain.repository.MPSessionRepository
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//TODO JPP connectivity!
class LoginViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                         private val sessionRepository: MPSessionRepository,
                                         private val accessTokenRepository: MPAccessTokenRepository)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<LoginViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<LoginNavigationEvent>() }

    init {
        _viewStates.addSource(sessionRepository.data()) { sessionData ->
            when (sessionData) {
                is CurrentSession -> { _navEvents.value = LoginNavigationEvent.BackToPrevious }
                is NoCurrentSessionAvailable -> { _viewStates.value = of(executeAccessTokenStep()) }
                is SessionCreated -> { _navEvents.value = LoginNavigationEvent.BackToPrevious }
                is UnableToCreateSession -> { _viewStates.value = of(LoginViewState.UnableToLogin) }
            }
        }

        _viewStates.addSource(accessTokenRepository.data()) { atData ->
            when (atData) {
                is Success -> { _viewStates.value = of(createOauthViewState(atData.data, false)) }
                is NoAccessTokenAvailable -> { _viewStates.value = of(LoginViewState.UnableToLogin) }
            }
        }
    }

    fun initialize() {
        launch { verifyUserLoggedIn() }
    }

    fun onUserRedirectedToUrl(redirectUrl: String, accessToken: AccessToken) {
        when {
            redirectUrl.contains("approved=true") -> { _viewStates.value = of(executeCreateSessionStep(accessToken)) }
            redirectUrl.contains("denied=true") -> _viewStates.value = of(createOauthViewState(accessToken, true))
            else -> _viewStates.value = of(LoginViewState.UnableToLogin)
        }
    }

    val viewStates: LiveData<HandledViewState<LoginViewState>> get() = _viewStates
    val navEvents: LiveData<LoginNavigationEvent> get() = _navEvents

    private suspend fun verifyUserLoggedIn() = withContext(dispatchers.default()) { sessionRepository.getCurrentSession() }
    private suspend fun getAccessToken() = withContext(dispatchers.default()) { accessTokenRepository.getAccessToken() }
    private suspend fun createSession(accessToken: AccessToken) = withContext(dispatchers.default()) { sessionRepository.createAndStoreSession(accessToken) }

    private fun createOauthViewState(accessToken: AccessToken, asReminder: Boolean) = LoginViewState.ShowOauth(
            url = "$authUrl/${accessToken.request_token}?redirect_to=$redirectUrl",
            interceptUrl = redirectUrl,
            accessToken = accessToken,
            reminder = asReminder
    )

    private fun executeAccessTokenStep() : LoginViewState  {
        launch { getAccessToken() }
        return LoginViewState.Loading
    }

    private fun executeCreateSessionStep(accessToken: AccessToken) :LoginViewState {
        launch { createSession(accessToken) }
        return LoginViewState.Loading
    }

    private companion object {
        const val authUrl = "https://www.themoviedb.org/authenticate/"
        const val redirectUrl = "http://www.mp.com/approved"
    }
}
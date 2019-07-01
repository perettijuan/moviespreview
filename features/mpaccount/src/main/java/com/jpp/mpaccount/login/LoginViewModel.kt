package com.jpp.mpaccount.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpaccount.login.LoginInteractor.LoginEvent
import com.jpp.mpaccount.login.LoginInteractor.OauthEvent
import com.jpp.mpaccount.login.LoginViewState.*
import com.jpp.mpdomain.AccessToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel used to support the login process.
 * Contains the view layer logic defined to provide the user the steps needed to login to
 * the system.
 */
class LoginViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                         private val loginInteractor: LoginInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<LoginViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<LoginNavigationEvent>() }
    private var loginAccessToken: AccessToken? = null

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(loginInteractor.loginEvents) { loginEvent ->
            when (loginEvent) {
                is LoginEvent.NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is LoginEvent.LoginSuccessful -> _navEvents.value = LoginNavigationEvent.ContinueToUserAccount
                is LoginEvent.LoginError -> _viewStates.value = of(ShowLoginError)
                is LoginEvent.UserAlreadyLogged -> _navEvents.value = LoginNavigationEvent.ContinueToUserAccount
                is LoginEvent.ReadyToLogin -> _viewStates.value = of(executeOauth())
            }
        }

        _viewStates.addSource(loginInteractor.oauthEvents) { oauthEvent ->
            when (oauthEvent) {
                is OauthEvent.NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is OauthEvent.OauthSuccessful -> _viewStates.value = of(createOauthViewState(oauthEvent))
                is OauthEvent.OauthError -> _viewStates.value = of(ShowLoginError)
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit() {
        clearState()
        _viewStates.value = of(verifyUserLoggedIn())
    }

    /**
     * Called when the user is redirected toa new URL in the Oauth step.
     */
    fun onUserRedirectedToUrl(redirectUrl: String) {
        when {
            redirectUrl.contains("approved=true") -> _viewStates.value = of(loginUser())
            redirectUrl.contains("denied=true") -> _viewStates.value = of(executeOauth())
            else -> _viewStates.value = of(ShowLoginError)
        }
    }

    /**
     * Called when the user retries the login.
     */
    fun onUserRetry() {
        _viewStates.value = of(executeOauth())
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<LoginViewState>> get() = _viewStates

    /**
     * Subscribe to this [LiveData] in order to get notified about navigation steps that
     * should be performed by the view.
     */
    val navEvents: LiveData<LoginNavigationEvent> get() = _navEvents

    private fun clearState() {
        loginAccessToken = null
    }

    private fun executeOauth(): LoginViewState {
        withLoginInteractor { fetchOauthData() }
        return ShowLoading
    }

    private fun verifyUserLoggedIn(): LoginViewState {
        withLoginInteractor { verifyUserLogged() }
        return ShowLoading
    }

    private fun loginUser(): LoginViewState {
        return loginAccessToken?.let {
            withLoginInteractor { loginUser(it) }
            ShowLoading
        } ?: ShowLoginError
    }

    private fun withLoginInteractor(action: LoginInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(loginInteractor) } }
    }

    private fun createOauthViewState(oauthEvent: OauthEvent.OauthSuccessful): LoginViewState {
        val asReminder = loginAccessToken != null
        loginAccessToken = oauthEvent.accessToken
        return ShowOauth(
                url = oauthEvent.url,
                interceptUrl = oauthEvent.interceptUrl,
                reminder = asReminder
        )
    }
}
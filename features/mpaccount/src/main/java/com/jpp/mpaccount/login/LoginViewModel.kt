package com.jpp.mpaccount.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpaccount.login.LoginInteractor.LoginEvent
import com.jpp.mpaccount.login.LoginInteractor.OauthEvent
import com.jpp.mpaccount.login.LoginViewState.*
import com.jpp.mpdomain.AccessToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the login process. The login implementation is implemented using
 * Oauth - therefore, this VM takes care of updating the view state of the [LoginFragment] in order
 * to properly render the view state needed to support Oauth.
 */
class LoginViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                         private val loginInteractor: LoginInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewState = MediatorLiveData<LoginViewState>()
    val viewState: LiveData<LoginViewState> get() = _viewState

    private val _navEvents = SingleLiveEvent<ContinueToUserAccount>()
    val navEvents: LiveData<ContinueToUserAccount> get() = _navEvents

    private var loginAccessToken: AccessToken? = null

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(loginInteractor.loginEvents) { loginEvent ->
            when (loginEvent) {
                is LoginEvent.NotConnectedToNetwork -> _viewState.value = ShowNotConnected
                is LoginEvent.LoginSuccessful -> _navEvents.value = ContinueToUserAccount
                is LoginEvent.LoginError -> _viewState.value = ShowLoginError
                is LoginEvent.UserAlreadyLogged -> _navEvents.value = ContinueToUserAccount
                is LoginEvent.ReadyToLogin -> executeOauth()
            }
        }

        _viewState.addSource(loginInteractor.oauthEvents) { oauthEvent ->
            when (oauthEvent) {
                is OauthEvent.NotConnectedToNetwork -> _viewState.value = ShowNotConnected
                is OauthEvent.OauthSuccessful -> _viewState.value = createOauthViewState(oauthEvent)
                is OauthEvent.OauthError -> _viewState.value = ShowLoginError
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
        loginAccessToken = null
        withLoginInteractor { verifyUserLogged() }
        _viewState.value = ShowLoading
    }

    /**
     * Called when the user is redirected toa new URL in the Oauth step.
     */
    fun onUserRedirectedToUrl(redirectUrl: String) {
        when {
            redirectUrl.contains("approved=true") -> loginUser()
            redirectUrl.contains("denied=true") -> executeOauth()
            else -> _viewState.value = ShowLoginError
        }
    }

    /**
     * Called when the user retries the login.
     */
    fun onUserRetry() {
        executeOauth()
    }

    private fun executeOauth() {
        withLoginInteractor { fetchOauthData() }
        _viewState.value = ShowLoading
    }

    private fun loginUser() {
        _viewState.value = loginAccessToken?.let {
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
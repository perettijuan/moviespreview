package com.jpp.mpaccount.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpaccount.login.LoginInteractor.LoginEvent
import com.jpp.mpaccount.login.LoginInteractor.OauthEvent
import com.jpp.mpdomain.AccessToken
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [MPViewModel] that supports the login process. The login implementation is implemented using
 * Oauth - therefore, this VM takes care of updating the view state of the [LoginFragment] in order
 * to properly render the view state needed to support Oauth.
 */
class LoginViewModel @Inject constructor(
    private val loginInteractor: LoginInteractor
) : MPViewModel() {

    private val _viewState = MediatorLiveData<LoginViewState>()
    val viewState: LiveData<LoginViewState> get() = _viewState

    private var loginAccessToken: AccessToken? = null
    private val retry: () -> Unit = { onInit(screenTitle) }

    private lateinit var screenTitle: String

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(loginInteractor.loginEvents) { loginEvent ->
            when (loginEvent) {
                is LoginEvent.NotConnectedToNetwork -> _viewState.value = LoginViewState.showNoConnectivityError(retry)
                is LoginEvent.LoginSuccessful -> continueToUserAccount()
                is LoginEvent.LoginError -> _viewState.value = LoginViewState.showUnknownError(retry)
                is LoginEvent.UserAlreadyLogged -> continueToUserAccount()
                is LoginEvent.ReadyToLogin -> executeOauth()
            }
        }

        _viewState.addSource(loginInteractor.oauthEvents) { oauthEvent ->
            when (oauthEvent) {
                is OauthEvent.NotConnectedToNetwork -> _viewState.value = LoginViewState.showNoConnectivityError(retry)
                is OauthEvent.OauthSuccessful -> _viewState.value = createOauthViewState(oauthEvent)
                is OauthEvent.OauthError -> _viewState.value = LoginViewState.showUnknownError(retry)
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(title: String) {
        screenTitle = title
        updateCurrentDestination(Destination.ReachedDestination(screenTitle))

        loginAccessToken = null
        withLoginInteractor { verifyUserLogged() }
        _viewState.value = LoginViewState.showLoading()
    }

    /**
     * Called when the user is redirected toa new URL in the Oauth step.
     */
    private fun onUserRedirectedToUrl(redirectUrl: String) {
        when {
            redirectUrl.contains("approved=true") -> loginUser()
            redirectUrl.contains("denied=true") -> executeOauth()
            else -> _viewState.value = LoginViewState.showUnknownError(retry)
        }
    }

    private fun executeOauth() {
        withLoginInteractor { fetchOauthData() }
        _viewState.value = LoginViewState.showLoading()
    }

    private fun loginUser() {
        _viewState.value = loginAccessToken?.let {
            withLoginInteractor { loginUser(it) }
            LoginViewState.showLoading()
        } ?: LoginViewState.showUnknownError(retry)
    }

    private fun withLoginInteractor(action: LoginInteractor.() -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                action(loginInteractor)
            }
        }
    }

    private fun createOauthViewState(oauthEvent: OauthEvent.OauthSuccessful): LoginViewState {
        val asReminder = loginAccessToken != null
        loginAccessToken = oauthEvent.accessToken
        return LoginViewState.showOauth(
                url = oauthEvent.url,
                interceptUrl = oauthEvent.interceptUrl,
                reminder = asReminder,
                redirectListener = { onUserRedirectedToUrl(it) }
        )
    }

    private fun continueToUserAccount() {
        navigateTo(Destination.InnerDestination(LoginFragmentDirections.toAccountFragment()))
    }
}

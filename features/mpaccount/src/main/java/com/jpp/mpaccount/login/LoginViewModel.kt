package com.jpp.mpaccount.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.usecase.GetAccessTokenUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LoginUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the login process. The login implementation is implemented using
 * Oauth - therefore, this VM takes care of updating the view state of the [LoginFragment] in order
 * to properly render the state needed to support Oauth.
 */
class LoginViewModel(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val loginUseCase: LoginUseCase,
    private val loginNavigator: LoginNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewState = MutableLiveData<LoginViewState>()
    internal val viewState: LiveData<LoginViewState> = _viewState

    // Do not make AccessToken parcelable b/c it belongs to the domain.
    private var loginAccessToken: AccessToken?
        set(value) {
            savedStateHandle.set(KEY_AT_SUCCESS, value?.success)
            savedStateHandle.set(KEY_AT_EXPIRES, value?.expires_at)
            savedStateHandle.set(KEY_AT_REQUEST_TOKEN, value?.request_token)
        }
        get() {
            val success = savedStateHandle.get<Boolean>(KEY_AT_SUCCESS) ?: return null
            val expiresAt = savedStateHandle.get<String>(KEY_AT_EXPIRES) ?: return null
            val requestToken = savedStateHandle.get<String>(KEY_AT_REQUEST_TOKEN) ?: return null

            return AccessToken(success, expiresAt, requestToken)
        }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit() {
        loginAccessToken = null
        _viewState.value = LoginViewState.showLoading()
        verifyUserLoggedAndContinueToAccount()
    }

    /**
     * Called when the user is redirected toa new URL in the Oauth step.
     */
    private fun onUserRedirectedToUrl(redirectUrl: String) {
        when {
            redirectUrl.contains("approved=true") -> loginUser()
            redirectUrl.contains("denied=true") -> initiateOauthProcess()
            else -> _viewState.value = LoginViewState.showUnknownError { onInit() }
        }
    }

    private fun verifyUserLoggedAndContinueToAccount() {
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getUserAccountUseCase.execute()
            }

            when (result) {
                is Try.Success -> loginNavigator.navigateToUserAccount()
                is Try.Failure -> processLoginVerificationFailure(result.cause)
            }
        }
    }

    private fun initiateOauthProcess() {
        viewModelScope.launch {
            val accessTokenResult = withContext(ioDispatcher) {
                getAccessTokenUseCase.execute()
            }

            when (accessTokenResult) {
                is Try.Success -> processAccessToken(accessTokenResult.value)
                is Try.Failure -> processFailure(accessTokenResult.cause)
            }
        }
    }

    private fun loginUser() {
        val accessToken = loginAccessToken

        if (accessToken == null) {
            _viewState.value = LoginViewState.showUnknownError { onInit() }
            return
        }

        viewModelScope.launch {
            _viewState.value = LoginViewState.showLoading()

            val loginResult = withContext(ioDispatcher) {
                loginUseCase.execute(accessToken)
            }

            when (loginResult) {
                is Try.Success -> loginNavigator.navigateToUserAccount()
                is Try.Failure -> processFailure(loginResult.cause)
            }
        }
    }

    private fun processAccessToken(accessToken: AccessToken) {
        val asReminder = loginAccessToken != null
        loginAccessToken = accessToken
        _viewState.value = LoginViewState.showOauth(
            url = accessToken.generateAuthenticationUrl(),
            interceptUrl = redirectUrl,
            reminder = asReminder,
            redirectListener = { onUserRedirectedToUrl(it) }
        )
    }

    private fun processLoginVerificationFailure(cause: Try.FailureCause) {
        when (cause) {
            is Try.FailureCause.NoConnectivity -> _viewState.value =
                LoginViewState.showNoConnectivityError { onInit() }
            is Try.FailureCause.Unknown -> _viewState.value =
                LoginViewState.showUnknownError { onInit() }
            is Try.FailureCause.UserNotLogged -> initiateOauthProcess()
        }
    }

    private fun processFailure(cause: Try.FailureCause) {
        when (cause) {
            is Try.FailureCause.NoConnectivity -> _viewState.value =
                LoginViewState.showNoConnectivityError { onInit() }
            else -> _viewState.value = LoginViewState.showUnknownError { onInit() }
        }
    }

    private fun AccessToken.generateAuthenticationUrl(): String {
        return "$authUrl/$request_token?redirect_to=$redirectUrl"
    }

    private companion object {
        const val authUrl = "https://www.themoviedb.org/authenticate"
        const val redirectUrl = "http://www.mp.com/approved"

        const val KEY_AT_SUCCESS = "KEY_AT_SUCCESS"
        const val KEY_AT_EXPIRES = "KEY_AT_EXPIRES"
        const val KEY_AT_REQUEST_TOKEN = "KEY_AT_REQUEST_TOKEN"
    }
}

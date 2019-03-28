package com.jpp.mp.screens.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.usecase.account.CreateSessionUseCase
import com.jpp.mpdomain.usecase.account.CreateSessionUseCase.CreateSessionResult
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.AccountInfo
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.UserNotLoggedIn
import com.jpp.mpdomain.usecase.account.GetAuthenticationDataUseCase
import com.jpp.mpdomain.usecase.account.GetAuthenticationDataUseCase.AuthenticationDataResult.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * TODO JPP -> the first thing this VM needs to do is to verify if the user is logged in.
 */
class AccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                           private val getAccountInfoUseCase: GetAccountInfoUseCase,
                                           private val getAuthenticationDataUseCase: GetAuthenticationDataUseCase,
                                           private val createSessionUseCase: CreateSessionUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<AccountViewState>() }

    fun init() {
        viewStateLiveData.value = AccountViewState.Loading
        launch {
            viewStateLiveData.value = getAccountInfo()
        }
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [AccountViewState].
     */
    fun viewState(): LiveData<AccountViewState> = viewStateLiveData

    /**
     * Called when the user has been authenticated successfully.
     */
    fun onUserAuthenticated(accessToken: AccessToken) {
        viewStateLiveData.value = AccountViewState.Loading
        launch {
            viewStateLiveData.value = createSession(accessToken)
        }
    }

    /**
     * Called when an error was detected while authenticating the user.
     */
    fun onUserFailedToAuthenticate() {

    }

    private suspend fun getAccountInfo(): AccountViewState = withContext(dispatchers.default()) {
        getAccountInfoUseCase
                .getAccountInfo()
                .let { ucResult ->
                    when (ucResult) {
                        is UserNotLoggedIn -> getLoginUrl()
                        is AccountInfo -> TODO()
                    }
                }
    }

    private suspend fun getLoginUrl(): AccountViewState = withContext(dispatchers.default()) {
        getAuthenticationDataUseCase
                .getAuthenticationData()
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> AccountViewState.ErrorNoConnectivity
                        is ErrorUnknown -> AccountViewState.ErrorUnknown
                        is Success -> AccountViewState.RenderlURL(url = ucResult.authenticationURL, interceptUrl = ucResult.redirectionUrl, accessToken = ucResult.accessToken)
                    }
                }
    }

    /**
     * TODO JPP chain this call with the UC that retrieves the user data.
     */
    private suspend fun createSession(accessToken: AccessToken): AccountViewState = withContext(dispatchers.default()) {
        createSessionUseCase
                .createSessionWith(accessToken)
                .let { ucResult ->
                    when (ucResult) {
                        is CreateSessionResult.ErrorNoConnectivity -> AccountViewState.ErrorNoConnectivity
                        is CreateSessionResult.ErrorUnknown -> AccountViewState.ErrorUnknown
                        is CreateSessionResult.Success -> AccountViewState.SessionCreated
                    }
                }
    }
}
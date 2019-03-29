package com.jpp.mp.screens.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.AccountInfo
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.UserNotLoggedIn
import com.jpp.mpdomain.usecase.session.CreateSessionUseCase
import com.jpp.mpdomain.usecase.session.CreateSessionUseCase.CreateSessionResult
import com.jpp.mpdomain.usecase.session.GetAuthenticationDataUseCase
import com.jpp.mpdomain.usecase.session.GetAuthenticationDataUseCase.AuthenticationDataResult.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * TODO JPP -> add tests for this VM
 */
class AccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                           private val getAccountInfoUseCase: GetAccountInfoUseCase,
                                           private val getAuthenticationDataUseCase: GetAuthenticationDataUseCase,
                                           private val createSessionUseCase: CreateSessionUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<AccountViewState>() }

    /**
     * Called when the fragment is initialized and the view is ready to render states.
     * Call to this method will trigger the execution of the use case to get the user
     * account information.
     */
    fun init() {
        pushLoadingAndFetchAccountInfo()
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [AccountViewState].
     */
    fun viewState(): LiveData<AccountViewState> = viewStateLiveData

    /**
     * Called when the user is redirected in the Oauth flow.
     * [redirectUrl] represents the redirection URl.
     * [oauthState] represents the last Oauth view state rendered.
     */
    fun onUserRedirectedToUrl(redirectUrl: String, oauthState: AccountViewState.Oauth) {
        when {
            redirectUrl.contains("approved=true") -> {
                viewStateLiveData.value = AccountViewState.Loading
                launch { viewStateLiveData.value = createSession(oauthState.accessToken) }
            }
            redirectUrl.contains("denied=true") -> viewStateLiveData.value = oauthState.copy(reminder = true)
            else -> viewStateLiveData.value = AccountViewState.ErrorUnknown
        }
    }

    /**
     * Called when an error is detected to attempt to execute the latest request.
     */
    fun retry() {
        pushLoadingAndFetchAccountInfo()
    }


    /**
     * Pushes the loading state into the view and starts the process to fetch the user account data.
     */
    private fun pushLoadingAndFetchAccountInfo() {
        viewStateLiveData.value = AccountViewState.Loading
        launch {
            viewStateLiveData.value = getAccountInfo()
        }
    }

    /**
     * Retrieves the information of the user's account. If the user is not logged, the output of [getLoginUrl]
     * will be rendered in order to start the Oauth process.
     */
    private suspend fun getAccountInfo(): AccountViewState = withContext(dispatchers.default()) {
        getAccountInfoUseCase
                .getAccountInfo()
                .let { ucResult ->
                    when (ucResult) {
                        is UserNotLoggedIn -> getLoginUrl()
                        is AccountInfo -> mapAccountInfo(ucResult.userAccount).let { AccountViewState.AccountInfo(headerItem = it) }
                        is GetAccountInfoUseCase.AccountInfoResult.ErrorNoConnectivity -> AccountViewState.ErrorNoConnectivity
                        is GetAccountInfoUseCase.AccountInfoResult.ErrorUnknown -> AccountViewState.ErrorUnknown
                    }
                }
    }

    /**
     * Called when the VM detects that the user is not logged. This triggers the execution of the UC
     * that retrieves an access token and creates the URL to execute the Oauth flow in the UI.
     */
    private suspend fun getLoginUrl(): AccountViewState = withContext(dispatchers.default()) {
        getAuthenticationDataUseCase
                .getAuthenticationData()
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> AccountViewState.ErrorNoConnectivity
                        is ErrorUnknown -> AccountViewState.ErrorUnknown
                        is Success -> AccountViewState.Oauth(url = ucResult.authenticationURL, interceptUrl = ucResult.redirectionUrl, accessToken = ucResult.accessToken)
                    }
                }
    }

    /**
     * Last step of the Oauth flow: called when the user has granted permissions to the application
     * to access the data of the account. This will fetch a session identifier and, if successful,
     * will execute [getAccountInfo] to fetch the user's account data with the session identifier created.
     */
    private suspend fun createSession(accessToken: AccessToken): AccountViewState = withContext(dispatchers.default()) {
        createSessionUseCase
                .createSessionWith(accessToken)
                .let { ucResult ->
                    when (ucResult) {
                        is CreateSessionResult.ErrorNoConnectivity -> AccountViewState.ErrorNoConnectivity
                        is CreateSessionResult.ErrorUnknown -> AccountViewState.ErrorUnknown
                        is CreateSessionResult.Success -> getAccountInfo()
                    }
                }
    }


    private fun mapAccountInfo(userAccount: UserAccount) = with(userAccount) {
        AccountHeaderItem(
                avatarUrl = avatar.gravatar.hash,
                userName = if (name.isEmpty()) username else name,
                accountName = username
        )
    }
}
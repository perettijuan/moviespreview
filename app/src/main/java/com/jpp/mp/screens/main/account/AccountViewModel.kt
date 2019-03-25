package com.jpp.mp.screens.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mpdomain.usecase.account.GetAccessTokenUseCase
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.*
import com.jpp.mpdomain.usecase.account.GetAccessTokenUseCase.AccessTokenResult.*
import kotlinx.coroutines.launch

/**
 * TODO JPP -> the first thing this VM needs to do is to verify if the user is logged in.
 */
class AccountViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                           private val getAccountInfoUseCase: GetAccountInfoUseCase,
                                           private val getAccessTokenUseCase: GetAccessTokenUseCase)
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

    private suspend fun getAccountInfo(): AccountViewState = withContext(dispatchers.default()) {
        getAccountInfoUseCase
                .getAccountInfo()
                .let { ucResult ->
                    when (ucResult) {
                        is UserNotLoggedIn -> getLoginUrl()
                        is AccountInfoAvailable -> TODO()
                    }
                }
    }

    private suspend fun getLoginUrl(): AccountViewState = withContext(dispatchers.default()) {
        getAccessTokenUseCase
                .getAccessToken()
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> AccountViewState.ErrorNoConnectivity
                        is ErrorUnknown -> AccountViewState.ErrorUnknown
                        is Success -> AccountViewState.RenderlURL(url = ucResult.authenticationURL, interceptUrl = ucResult.redirectionUrl)
                    }
                }
    }
}
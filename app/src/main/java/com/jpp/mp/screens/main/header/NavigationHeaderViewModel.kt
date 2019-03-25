package com.jpp.mp.screens.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.*
import kotlinx.coroutines.launch

class NavigationHeaderViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                    private val getAccountInfoUseCase: GetAccountInfoUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<HeaderViewState>() }

    fun init() {
        viewStateLiveData.value = HeaderViewState.Loading
        launch {
            viewStateLiveData.value = getAccountInfo()
        }
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [HeaderViewState].
     */
    fun viewState(): LiveData<HeaderViewState> = viewStateLiveData


    private suspend fun getAccountInfo(): HeaderViewState = withContext(dispatchers.default()) {
        getAccountInfoUseCase
                .getAccountInfo()
                .let { ucResult ->
                    when (ucResult) {
                        is UserNotLoggedIn -> HeaderViewState.NotLogged
                        is AccountInfoAvailable -> TODO()
                    }
                }
    }

}
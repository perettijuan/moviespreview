package com.jpp.mp.screens.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mp.screens.SingleLiveEvent
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpdomain.usecase.account.GetAccountInfoUseCase.AccountInfoResult.*
import kotlinx.coroutines.launch

/**
 * [MPScopedViewModel] to handle the state of the NavigationHeaderFragment. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It exposes a single output in a LiveData object that receives [HeaderViewState] updates as soon
 * as any new state is identified by the ViewModel.
 */
class NavigationHeaderViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                    private val getAccountInfoUseCase: GetAccountInfoUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<HeaderViewState>() }
    private val navigationEvents by lazy { SingleLiveEvent<HeaderNavigationEvent>() }

    /**
     * Called on initialization of the header fragment.
     * Each time this method is called it will fetch the user account data and will update
     * the UI.
     * The updates will be posted to the [LiveData] object provided by [viewState()].
     */
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

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<HeaderNavigationEvent> = navigationEvents

    /**
     * Called when the user attempts to navigate to the login screen.
     */
    fun onUserNavigatesToLogin() {
        navigationEvents.value = HeaderNavigationEvent.ToLogin
    }

    /**
     * Called when the user attempts to navigate to the account details.
     */
    fun onUserNavigatesToAccountDetails() {
        navigationEvents.value = HeaderNavigationEvent.ToUserAccount
    }

    /**
     * Executes the use case to fetch the user account info. If an error is detected, the
     * login view will be shown in order to let the user attempt a login.
     */
    private suspend fun getAccountInfo(): HeaderViewState = withContext(dispatchers.default()) {
        getAccountInfoUseCase
                .getAccountInfo()
                .let { ucResult ->
                    when (ucResult) {
                        is AccountInfo -> mapAccountInfo(ucResult.userAccount).let { HeaderViewState.WithInfo(accountInfo = it) }
                        else -> HeaderViewState.Login
                    }
                }
    }

    private fun mapAccountInfo(userAccount: UserAccount) = with(userAccount) {
        HeaderAccountInfo(
                avatarUrl = avatar.gravatar.hash,
                userName = if (name.isEmpty()) username else name,
                accountName = username
        )
    }
}
package com.jpp.mp.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPViewModel] that supports the [NavigationHeaderFragment] behavior. It retrieves the
 * user account data and updates the view state that the Fragment takes care of rendering.
 */
class NavigationHeaderViewModel @Inject constructor(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : MPViewModel() {

    private val _viewState = MutableLiveData<HeaderViewState>()
    internal val viewState: LiveData<HeaderViewState> = _viewState

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit() {
        _viewState.value = HeaderViewState.showLoading()
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getUserAccountUseCase.execute()
            }

            when (result) {
                is Try.Success -> processUserAccount(result.value)
                is Try.Failure -> processFailureCase()
            }
        }
    }

    /**
     * Called when the the navigate to login option is selected in the UI.
     */
    internal fun onNavigateToLoginSelected() {
        navigateTo(Destination.MPAccount)
    }

    /**
     * Called when the user attempts to navigate to the account details.
     */
    internal fun onNavigateToAccountDetailsSelected() {
        navigateTo(Destination.MPAccount)
    }

    private fun processUserAccount(userAccount: UserAccount) {
        _viewState.value = HeaderViewState.showAccountWithAvatar(
            avatarUrl = userAccount.avatar.getFullUrl(),
            userName = userAccount.getUserName(),
            accountName = userAccount.username,
            avatarCallback = { userAvatarCallback(userAccount) }
        )
    }

    private fun processFailureCase() {
        _viewState.value = HeaderViewState.showLogin()
    }

    /**
     * Method called when the UI is unable to download the user avatar. The VM
     * will render a new [HeaderViewState] that will show the user's name letter
     * instead of the user's avatar.
     */
    private fun userAvatarCallback(userAccount: UserAccount) {
        _viewState.value = HeaderViewState.showAccountWithLetter(
            userName = userAccount.getUserName(),
            accountName = userAccount.username,
            defaultLetter = userAccount.getUserLetter()
        )
    }
}

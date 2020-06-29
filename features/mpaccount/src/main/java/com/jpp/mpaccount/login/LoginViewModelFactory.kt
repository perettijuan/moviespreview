package com.jpp.mpaccount.login

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetAccessTokenUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LoginUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [LoginViewModel] instances
 * with the dependencies provided by Dagger.
 */
class LoginViewModelFactory @Inject constructor(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val loginUseCase: LoginUseCase,
    private val loginNavigator: LoginNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<LoginViewModel> {
    override fun create(handle: SavedStateHandle): LoginViewModel {
        return LoginViewModel(
            getUserAccountUseCase,
            getAccessTokenUseCase,
            loginUseCase,
            loginNavigator,
            ioDispatcher,
            handle
        )
    }
}

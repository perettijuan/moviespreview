package com.jpp.mpaccount.login

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [LoginViewModel] instances
 * with the dependencies provided by Dagger.
 */
class LoginViewModelFactory @Inject constructor(
    private val loginInteractor: LoginInteractor,
    private val loginNavigator: LoginNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<LoginViewModel> {
    override fun create(handle: SavedStateHandle): LoginViewModel {
        return LoginViewModel(loginInteractor, loginNavigator, ioDispatcher)
    }
}
package com.jpp.mpaccount.account

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetUserAccountMoviesUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LogOutUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [UserAccountViewModel] instances
 * with the dependencies provided by Dagger.
 */
class UserAccountViewModelFactory @Inject constructor(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val getMoviesUseCase: GetUserAccountMoviesUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val userAccountNavigator: UserAccountNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<UserAccountViewModel> {
    override fun create(handle: SavedStateHandle): UserAccountViewModel {
        return UserAccountViewModel(
            getUserAccountUseCase,
            getMoviesUseCase,
            logOutUseCase,
            userAccountNavigator,
            ioDispatcher
        )
    }
}

package com.jpp.mpaccount.account

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [UserAccountViewModel] instances
 * with the dependencies provided by Dagger.
 */
class UserAccountViewModelFactory @Inject constructor(
    private val getUserAccountUseCase: GetUserAccountUseCase,
    private val getUserAccountMoviePageUseCase: GetUserAccountMoviePageUseCase,
    private val userAccountNavigator: UserAccountNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<UserAccountViewModel> {
    override fun create(handle: SavedStateHandle): UserAccountViewModel {
        return UserAccountViewModel(
            getUserAccountUseCase,
            getUserAccountMoviePageUseCase,
            userAccountNavigator,
            ioDispatcher
        )
    }
}
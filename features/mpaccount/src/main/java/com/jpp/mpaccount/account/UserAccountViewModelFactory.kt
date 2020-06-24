package com.jpp.mpaccount.account

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [UserAccountViewModel] instances
 * with the dependencies provided by Dagger.
 */
class UserAccountViewModelFactory @Inject constructor(
    private val accountInteractor: UserAccountInteractor,
    private val imagesPathInteractor: ImagesPathInteractor,
    private val userAccountNavigator: UserAccountNavigator
) : ViewModelAssistedFactory<UserAccountViewModel> {
    override fun create(handle: SavedStateHandle): UserAccountViewModel {
        return UserAccountViewModel(accountInteractor, imagesPathInteractor, userAccountNavigator)
    }
}
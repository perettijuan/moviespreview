package com.jpp.mpcredits

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetCreditsUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [CreditsViewModel] instances
 * with the dependencies provided by Dagger.
 * Note that Dagger inject's this object and this object takes care of creating
 * the [CreditsViewModel] instance needed.
 */
class CreditsViewModelFactory @Inject constructor(
    private val getCreditsUseCase: GetCreditsUseCase,
    private val creditNavigator: CreditNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<CreditsViewModel> {

    override fun create(handle: SavedStateHandle): CreditsViewModel {
        return CreditsViewModel(getCreditsUseCase, creditNavigator, ioDispatcher, handle)
    }
}

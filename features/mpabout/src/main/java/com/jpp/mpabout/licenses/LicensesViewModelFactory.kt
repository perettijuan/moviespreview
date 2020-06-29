package com.jpp.mpabout.licenses

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetAppLicensesUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [LicensesViewModel] instances
 * with the dependencies provided by Dagger.
 */
class LicensesViewModelFactory @Inject constructor(
    private val getAppLicensesUseCase: GetAppLicensesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<LicensesViewModel> {
    override fun create(handle: SavedStateHandle): LicensesViewModel {
        return LicensesViewModel(
            getAppLicensesUseCase,
            ioDispatcher
        )
    }
}

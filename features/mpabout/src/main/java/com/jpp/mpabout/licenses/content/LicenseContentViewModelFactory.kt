package com.jpp.mpabout.licenses.content

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.FindAppLicenseUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [LicenseContentViewModel] instances
 * with the dependencies provided by Dagger.
 */
class LicenseContentViewModelFactory @Inject constructor(
    private val findAppLicenseUseCase: FindAppLicenseUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<LicenseContentViewModel> {
    override fun create(handle: SavedStateHandle): LicenseContentViewModel {
        return LicenseContentViewModel(findAppLicenseUseCase, ioDispatcher, handle)
    }
}

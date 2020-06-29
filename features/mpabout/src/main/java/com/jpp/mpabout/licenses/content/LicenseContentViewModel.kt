package com.jpp.mpabout.licenses.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.usecase.FindAppLicenseUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the [LicenseContentFragment]. When initialized, the VM
 * takes care of updating the UI state in order to render the content of a particular license.
 */
class LicenseContentViewModel(
    private val findAppLicenseUseCase: FindAppLicenseUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewState = MutableLiveData<LicenseContentViewState>()
    internal val viewState: LiveData<LicenseContentViewState> = _viewState

    private var licenseId: Int
        set(value) = savedStateHandle.set(LICENSE_ID_KEY, value)
        get() = savedStateHandle.get(LICENSE_ID_KEY) ?: 0

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(licenseId: Int) {
        this.licenseId = licenseId
        pushLoadingAndFetchAppLicense()
    }

    private fun pushLoadingAndFetchAppLicense() {
        _viewState.value = LicenseContentViewState.showLoading()
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                findAppLicenseUseCase.execute(licenseId)
            }

            _viewState.value = when (result) {
                is Try.Success -> LicenseContentViewState.showContent(result.value.url)
                else -> LicenseContentViewState.showError { pushLoadingAndFetchAppLicense() }
            }
        }
    }

    private companion object {
        const val LICENSE_ID_KEY = "LICENSE_ID_KEY"
    }
}

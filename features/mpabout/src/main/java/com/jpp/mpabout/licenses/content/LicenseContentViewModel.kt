package com.jpp.mpabout.licenses.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mpdomain.usecase.FindAppLicenseUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPViewModel] that supports the [LicenseContentFragment]. When initialized, the VM
 * takes care of updating the UI state in order to render the content of a particular license.
 */
class LicenseContentViewModel @Inject constructor(
    private val findAppLicenseUseCase: FindAppLicenseUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : MPViewModel() {

    private val _viewState = MutableLiveData<LicenseContentViewState>()
    val viewState: LiveData<LicenseContentViewState> get() = _viewState

    private var licenseId: Int = 0

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(licenseId: Int) {
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
}

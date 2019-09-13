package com.jpp.mpabout.licenses.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpabout.AboutInteractor
import com.jpp.mpabout.AboutInteractor.LicensesEvent.Success
import com.jpp.mpabout.AboutInteractor.LicensesEvent.UnknownError
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the [LicenseContentFragment]. When initialized, the VM
 * takes care of updating the UI state in order to render the content of a particular license.
 */
class LicenseContentViewModel @Inject constructor(coroutineDispatchers: CoroutineDispatchers,
                                                  private val aboutInteractor: AboutInteractor)
    : MPScopedViewModel(coroutineDispatchers) {

    private val _viewState = MediatorLiveData<LicenseContentViewState>()
    val viewState: LiveData<LicenseContentViewState> get() = _viewState

    private var licenseId: Int = 0

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(aboutInteractor.licenseEvents) { event ->
            when (event) {
                is UnknownError -> LicenseContentViewState.showError { pushLoadingAndFetchAppLicenses() }
                is Success -> LicenseContentViewState.showContent(event.results.licenses.first { license -> license.id == licenseId }.url)
            }.let { _viewState.value = it }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(licenseId: Int) {
        this.licenseId = licenseId
        pushLoadingAndFetchAppLicenses()
    }


    private fun withInteractor(action: AboutInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(aboutInteractor) } }
    }

    private fun pushLoadingAndFetchAppLicenses() {
        withInteractor { fetchAppLicenses() }
        _viewState.value = LicenseContentViewState.showLoading()
    }
}
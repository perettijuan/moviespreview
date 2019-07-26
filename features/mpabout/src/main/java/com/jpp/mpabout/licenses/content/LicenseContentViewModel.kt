package com.jpp.mpabout.licenses.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mpabout.AboutInteractor
import com.jpp.mpabout.AboutInteractor.LicensesEvent.Success
import com.jpp.mpabout.AboutInteractor.LicensesEvent.UnknownError
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LicenseContentViewModel @Inject constructor(coroutineDispatchers: CoroutineDispatchers,
                                                  private val aboutInteractor: AboutInteractor)
    : MPScopedViewModel(coroutineDispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<LicenseContentViewState>>() }
    private var licenseId: Int = 0

    init {
        _viewStates.addSource(aboutInteractor.licenseEvents) { event ->
            when (event) {
                is UnknownError -> LicenseContentViewState.showError { pushLoadingAndFetchAppLicenses() }
                is Success -> LicenseContentViewState.showContent(event.results.licenses.first { license -> license.id == licenseId }.url)
            }.let { _viewStates.value = HandledViewState.of(it) }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(licenseId: Int) {
        this.licenseId = licenseId
        pushLoadingAndFetchAppLicenses()
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<LicenseContentViewState>> get() = _viewStates


    private fun withInteractor(action: AboutInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(aboutInteractor) } }
    }

    private fun pushLoadingAndFetchAppLicenses() {
        withInteractor { fetchAppLicenses() }
        _viewStates.value = HandledViewState.of(LicenseContentViewState.showLoading())
    }
}
package com.jpp.mpabout.licenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpabout.AboutInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import  com.jpp.mpabout.AboutInteractor.LicensesEvent.*
import com.jpp.mpdomain.License

class LicensesViewModel @Inject constructor(coroutineDispatchers: CoroutineDispatchers,
                                            private val aboutInteractor: AboutInteractor)
    : MPScopedViewModel(coroutineDispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<LicensesViewState>>() }

    init {
        _viewStates.addSource(aboutInteractor.licenseEvents) { event ->
            when (event) {
                is UnknownError -> LicensesViewState.showError { pushLoadingAndFetchAppLicenses() }
                is Success -> LicensesViewState.showContent(event.results.licenses.map { mapLicense(it) })
            }.let { _viewStates.value = of(it) }
        }
    }


    /**
     * Called when the view is initialized.
     */
    fun onInit() {
        pushLoadingAndFetchAppLicenses()
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<LicensesViewState>> get() = _viewStates


    private fun withInteractor(action: AboutInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(aboutInteractor) } }
    }

    private fun pushLoadingAndFetchAppLicenses() {
        withInteractor { fetchAppLicenses() }
        _viewStates.value = of(LicensesViewState.showLoading())
    }

    private fun mapLicense(license: License): LicenseItem = with(license) { LicenseItem(id = id, name = name) }
}
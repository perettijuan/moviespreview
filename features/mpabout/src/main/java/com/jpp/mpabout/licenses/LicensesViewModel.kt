package com.jpp.mpabout.licenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpabout.AboutInteractor
import com.jpp.mpabout.AboutInteractor.LicensesEvent.Success
import com.jpp.mpabout.AboutInteractor.LicensesEvent.UnknownError
import com.jpp.mpdomain.License
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [MPViewModel] that supports the list of [License]s that the application uses.
 */
class LicensesViewModel @Inject constructor(
    private val aboutInteractor: AboutInteractor
) : MPViewModel() {

    private val _viewState = MediatorLiveData<LicensesViewState>()
    val viewState: LiveData<LicensesViewState> get() = _viewState

    private val _navEvents = MutableLiveData<HandledEvent<GoToLicenseContentEvent>>()
    val navEvents: LiveData<HandledEvent<GoToLicenseContentEvent>> get() = _navEvents

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(aboutInteractor.licenseEvents) { event ->
            when (event) {
                is UnknownError -> LicensesViewState.showError { pushLoadingAndFetchAppLicenses() }
                is Success -> LicensesViewState.showContent(event.results.licenses.map { mapLicense(it) })
            }.let { _viewState.value = it }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(screenTitle: String) {
        updateCurrentDestination(Destination.ReachedDestination(screenTitle))
        pushLoadingAndFetchAppLicenses()
    }

    /**
     * Called when an item is selected in the list of licenses.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onLicenseSelected(item: LicenseItem) {
        _navEvents.value = of(GoToLicenseContentEvent(item.id))
    }

    /**
     * Helper function to perform an action with the [aboutInteractor] in a background
     * thread.
     */
    private fun withInteractor(action: AboutInteractor.() -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                action(aboutInteractor)
            }
        }
    }

    /**
     * Push the loading view state and fetch the application licenses in a
     * background thread.
     */
    private fun pushLoadingAndFetchAppLicenses() {
        withInteractor { fetchAppLicenses() }
        _viewState.value = LicensesViewState.showLoading()
    }

    private fun mapLicense(license: License): LicenseItem = with(license) { LicenseItem(id = id, name = name) }
}

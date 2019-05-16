package com.jpp.mp.screens.main.licenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mpdomain.License
import com.jpp.mpdomain.usecase.licenses.GetAppLicensesUseCase
import com.jpp.mpdomain.usecase.licenses.GetLicensesResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] to handle the state of the LicensesFragment. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It exposes a single output in a LiveData object that receives [LicensesViewState] updates as soon
 * as any new state is identified by the ViewModel.
 */
class LicensesViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                            private val getAppLicensesUseCase: GetAppLicensesUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<LicensesViewState>() }
    private val navigationEvents by lazy { SingleLiveEvent<LicensesNavEvent>() }
    private lateinit var retryFunc: () -> Unit

    fun init() {
        retryFunc = { pushLoadingAndFetchLicenses() }
        pushLoadingAndFetchLicenses()
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [LicensesViewState].
     */
    fun viewState(): LiveData<LicensesViewState> = viewStateLiveData

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<LicensesNavEvent> = navigationEvents

    /**
     * Called in order to execute the last attempt to fetch the credits.
     */
    fun retry() {
        if (viewStateLiveData.value is LicensesViewState.ErrorUnknown) {
            retryFunc.invoke()
        }
    }

    /**
     * Called when the user selects a license from the list of [LicenseItem].
     * A new navigation event will be posted to navEvents().
     */
    fun onUserSelectedLicense(licenseItem: LicenseItem) {
        navigationEvents.value = LicensesNavEvent.ToLicenseContent(licenseName = licenseItem.name, licenseId = licenseItem.id)
    }

    /**
     * Pushes the loading state into the view and starts the process to fetch the app results.
     */
    private fun pushLoadingAndFetchLicenses() {
        viewStateLiveData.value = LicensesViewState.Loading
        launch {
            viewStateLiveData.value = fetchAppLicenses()
        }
    }

    /**
     * Fetches the results list used by the application.
     * @return a [LicensesViewState] that is posted in viewState in order
     * to update the UI.
     */
    private suspend fun fetchAppLicenses(): LicensesViewState = withContext(dispatchers.default()) {
        getAppLicensesUseCase
                .getAppLicences()
                .let { ucResult ->
                    when (ucResult) {
                        is GetLicensesResult.ErrorUnknown -> LicensesViewState.ErrorUnknown
                        is GetLicensesResult.Success -> {
                            LicensesViewState.Loaded(ucResult.results.licenses.map { mapLicense(it) })
                        }
                    }
                }
    }


    private fun mapLicense(license: License): LicenseItem = with(license) {
        LicenseItem(
                id = id,
                name = name
        )
    }
}
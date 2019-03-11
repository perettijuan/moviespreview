package com.jpp.moviespreview.screens.main.licenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.moviespreview.screens.CoroutineDispatchers
import com.jpp.moviespreview.screens.MPScopedViewModel
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
     * Called in order to execute the last attempt to fetch the credits.
     */
    fun retry() {
        if (viewStateLiveData.value is LicensesViewState.ErrorUnknown) {
            retryFunc.invoke()
        }
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
package com.jpp.mp.screens.main.licenses.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.usecase.licenses.GetLicenceResult
import com.jpp.mpdomain.usecase.licenses.GetLicenseUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] to handle the state of the LicenseContentFragmentDeprecated. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It exposes a single output in a LiveData object that receives [LicenseViewState] updates as soon
 * as any new state is identified by the ViewModel.
 */
//TODO delete ME
class LicenseContentViewModelDeprecated @Inject constructor(dispatchers: CoroutineDispatchers,
                                                            private val getLicenseUseCase: GetLicenseUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<LicenseViewState>() }
    private lateinit var retryFunc: () -> Unit


    /**
     * Called every time the ViewModel needs to be initialized.
     * When called, it executes the use case and retrieves the details of the selected
     * license.
     * The result will be posted to viewState().
     */
    fun init(licenseId: Int) {
        retryFunc = { pushLoadingAndFetchLicenseContent(licenseId) }
        pushLoadingAndFetchLicenseContent(licenseId)
    }


    /**
     * Subscribe to this [LiveData] in order to get updates of the [LicenseViewState].
     */
    fun viewState(): LiveData<LicenseViewState> = viewStateLiveData

    /**
     * Called in order to execute the last attempt to fetch the credits.
     */
    fun retry() {
        if (viewStateLiveData.value is LicenseViewState.ErrorUnknown) {
            retryFunc.invoke()
        }
    }

    /**
     * Pushes the loading state into the view and starts the process to fetch the contentViewState of the license.
     */
    private fun pushLoadingAndFetchLicenseContent(licenseId: Int) {
        viewStateLiveData.value = LicenseViewState.Loading
        launch {
            viewStateLiveData.value = fetchLicenseContent(licenseId)
        }
    }

    /**
     * Fetches the contentViewState of the licenses identified with [licenseId].
     * @return a [LicenseViewState] that is posted in viewState in order
     * to update the UI.
     */
    private suspend fun fetchLicenseContent(licenseId: Int) : LicenseViewState = withContext(dispatchers.default()) {
        getLicenseUseCase
                .getLicense(licenseId)
                .let { ucResult ->
                    when (ucResult) {
                        is GetLicenceResult.ErrorUnknown -> LicenseViewState.ErrorUnknown
                        is GetLicenceResult.Success -> LicenseViewState.Loaded(contentUrl = ucResult.licence.url)
                    }
                }
    }
}
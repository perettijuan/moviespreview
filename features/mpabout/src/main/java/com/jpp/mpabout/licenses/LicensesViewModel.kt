package com.jpp.mpabout.licenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mpdomain.License
import com.jpp.mpdomain.usecase.GetAppLicensesUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the list of [License]s that the application uses.
 */
class LicensesViewModel(
    private val getAppLicensesUseCase: GetAppLicensesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState = MutableLiveData<LicensesViewState>()
    internal val viewState: LiveData<LicensesViewState> = _viewState

    private val _navEvents = MutableLiveData<HandledEvent<GoToLicenseContentEvent>>()
    internal val navEvents: LiveData<HandledEvent<GoToLicenseContentEvent>> = _navEvents

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit() {
        pushLoadingAndFetchAppLicenses()
    }

    /**
     * Called when an item is selected in the list of licenses.
     * A new state is posted in navEvents() in order to handle the event.
     */
    internal fun onLicenseSelected(item: LicenseItem) {
        _navEvents.value = of(GoToLicenseContentEvent(item.id))
    }

    /**
     * Push the loading view state and fetch the application licenses in a
     * background thread.
     */
    private fun pushLoadingAndFetchAppLicenses() {
        _viewState.value = LicensesViewState.showLoading()
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getAppLicensesUseCase.execute()
            }

            _viewState.value = when (result) {
                is Try.Success -> _viewState.value?.showContent(result.value.licenses.map { license -> license.toLicenseItem() })
                else -> _viewState.value?.showError { pushLoadingAndFetchAppLicenses() }
            }
        }
    }

    private fun License.toLicenseItem(): LicenseItem = LicenseItem(id = id, name = name)
}

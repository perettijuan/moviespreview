package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the about section. The VM retrieves
 * the data from the underlying layers and maps the business
 * data to UI data, producing a [AboutViewState] that represents the configuration of the view.
 */
class AboutViewModel(
    private val appVersionRepository: AppVersionRepository,
    private val aboutUrlRepository: AboutUrlRepository,
    private val aboutNavigator: AboutNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState = MutableLiveData<AboutViewState>()
    internal val viewState: LiveData<AboutViewState> = _viewState

    private val _navEvents = MutableLiveData<HandledEvent<AboutNavEvent>>()
    internal val navEvents: LiveData<HandledEvent<AboutNavEvent>> = _navEvents

    private val supportedAboutItems = listOf(
        AboutItem.RateApp,
        AboutItem.ShareApp,
        AboutItem.PrivacyPolicy,
        AboutItem.BrowseAppCode,
        AboutItem.Licenses,
        AboutItem.TheMovieDbTermsOfUse
    )

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit() {
        viewModelScope.launch {
            _viewState.value = AboutViewState.showLoading()

            val currentAppVersion = withContext(ioDispatcher) {
                appVersionRepository.getCurrentAppVersion().version
            }
            _viewState.value = _viewState.value?.showContent(
                appVersion = "v $currentAppVersion",
                aboutItems = supportedAboutItems
            )
        }
    }

    /**
     * Called when the user has selected an item from the about items section.
     */
    internal fun onUserSelectedAboutItem(aboutItem: AboutItem) {
        if (aboutItem is AboutItem.Licenses) {
            aboutNavigator.navigateToLicenses()
            return
        }

        viewModelScope.launch {
            val navEvent = withContext(ioDispatcher) {
                when (aboutItem) {
                    is AboutItem.BrowseAppCode -> AboutNavEvent.InnerNavigation(aboutUrlRepository.getCodeRepoUrl().url)
                    is AboutItem.TheMovieDbTermsOfUse -> AboutNavEvent.InnerNavigation(aboutUrlRepository.getTheMovieDbTermOfUseUrl().url)
                    is AboutItem.PrivacyPolicy -> AboutNavEvent.OuterNavigation(aboutUrlRepository.getPrivacyPolicyUrl().url)
                    is AboutItem.RateApp -> AboutNavEvent.OpenGooglePlay(aboutUrlRepository.getGPlayAppUrl().url)
                    is AboutItem.ShareApp -> AboutNavEvent.OpenSharing(aboutUrlRepository.getSharingUrl().url)
                    else -> throw IllegalStateException("Unknown item selected $aboutItem")
                }
            }
            _navEvents.value = of(navEvent)
        }
    }

    /**
     * Called when the app fails to expanded the Google Play app in the device.
     */
    internal fun onFailedToOpenPlayStore() {
        viewModelScope.launch {
            val aboutUrl = withContext(ioDispatcher) {
                aboutUrlRepository.getGPlayWebUrl()
            }
            _navEvents.value = of(AboutNavEvent.OpenGooglePlay(aboutUrl.url))
        }
    }
}

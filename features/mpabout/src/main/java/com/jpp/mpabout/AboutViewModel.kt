package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository

/**
 * [MPViewModel] that supports the about section. The VM retrieves
 * the data from the underlying layers using the provided [AboutInteractor] and maps the business
 * data to UI data, producing a [AboutViewState] that represents the configuration of the view.
 */
class AboutViewModel(
    private val appVersionRepository: AppVersionRepository,
    private val aboutUrlRepository: AboutUrlRepository
) : MPViewModel() {

    private val _viewState = MediatorLiveData<AboutViewState>()
    val viewState: LiveData<AboutViewState> = _viewState

    private val _navEvents = MutableLiveData<HandledEvent<AboutNavEvent>>()
    val navEvents: LiveData<HandledEvent<AboutNavEvent>> = _navEvents

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
    fun onInit() {
        _viewState.value = AboutViewState.showContent(
            appVersion = "v ${appVersionRepository.getCurrentAppVersion().version}",
            aboutItems = supportedAboutItems
        )
    }

    /**
     * Called when the user has selected an item from the about items section.
     */
    fun onUserSelectedAboutItem(aboutItem: AboutItem) {
        if (aboutItem is AboutItem.Licenses) {
            navigateTo(Destination.InnerDestination(AboutFragmentDirections.licensesFragment()))
            return
        }

        when (aboutItem) {
            is AboutItem.BrowseAppCode -> AboutNavEvent.InnerNavigation(aboutUrlRepository.getCodeRepoUrl().url)
            is AboutItem.TheMovieDbTermsOfUse -> AboutNavEvent.InnerNavigation(aboutUrlRepository.getTheMovieDbTermOfUseUrl().url)
            is AboutItem.PrivacyPolicy -> AboutNavEvent.OuterNavigation(aboutUrlRepository.getPrivacyPolicyUrl().url)
            is AboutItem.RateApp -> AboutNavEvent.OpenGooglePlay(aboutUrlRepository.getGPlayAppUrl().url)
            is AboutItem.ShareApp -> AboutNavEvent.OpenSharing(aboutUrlRepository.getSharingUrl().url)
            else -> throw IllegalStateException("Unknown item selected $aboutItem")
        }.let { selectedItem ->
            _navEvents.value = of(selectedItem)
        }
    }

    /**
     * Called when the app fails to expanded the Google Play app in the device.
     */
    fun onFailedToOpenPlayStore() {
        _navEvents.value = of(AboutNavEvent.OpenGooglePlay(aboutUrlRepository.getGPlayWebUrl().url))
    }
}

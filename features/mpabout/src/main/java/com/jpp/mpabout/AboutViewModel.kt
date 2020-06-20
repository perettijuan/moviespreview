package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.AboutUrl
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository
import javax.inject.Inject

/**
 * [MPViewModel] that supports the about section. The VM retrieves
 * the data from the underlying layers using the provided [AboutInteractor] and maps the business
 * data to UI data, producing a [AboutViewState] that represents the configuration of the view.
 */
class AboutViewModel @Inject constructor(
    private val appVersionRepository: AppVersionRepository,
    private val aboutUrlRepository: AboutUrlRepository
) : MPViewModel() {

    private val _viewState = MediatorLiveData<AboutViewState>()
    val viewState: LiveData<AboutViewState> = _viewState

    private val _navEvents = MutableLiveData<HandledEvent<AboutNavEvent>>()
    val navEvents: LiveData<HandledEvent<AboutNavEvent>> = _navEvents

    private lateinit var selectedItem: AboutItem
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
        selectedItem = aboutItem
        when (aboutItem) {
            is AboutItem.BrowseAppCode -> processAboutUrl(aboutUrlRepository.getCodeRepoUrl())
            is AboutItem.TheMovieDbTermsOfUse -> processAboutUrl(aboutUrlRepository.getTheMovieDbTermOfUseUrl())
            is AboutItem.PrivacyPolicy -> processAboutUrl(aboutUrlRepository.getPrivacyPolicyUrl())
            is AboutItem.RateApp -> processAboutUrl(aboutUrlRepository.getGPlayAppUrl())
            is AboutItem.ShareApp -> processAboutUrl(aboutUrlRepository.getSharingUrl())
            is AboutItem.Licenses -> navigateTo(Destination.InnerDestination(AboutFragmentDirections.licensesFragment()))
        }
    }

    /**
     * Called when the app fails to expanded the Google Play app in the device.
     */
    fun onFailedToOpenPlayStore() {
        processAboutUrl(aboutUrlRepository.getGPlayWebUrl())
    }

    private fun processAboutUrl(aboutUrl: AboutUrl) {
        when (selectedItem) {
            is AboutItem.BrowseAppCode -> AboutNavEvent.InnerNavigation(aboutUrl.url)
            is AboutItem.TheMovieDbTermsOfUse -> AboutNavEvent.InnerNavigation(aboutUrl.url)
            is AboutItem.PrivacyPolicy -> AboutNavEvent.OuterNavigation(aboutUrl.url)
            is AboutItem.RateApp -> AboutNavEvent.OpenGooglePlay(aboutUrl.url)
            is AboutItem.ShareApp -> AboutNavEvent.OpenSharing(aboutUrl.url)
            else -> throw IllegalStateException("Unknown item selected $selectedItem")
        }.let {
            _navEvents.value = of(it)
        }
    }
}

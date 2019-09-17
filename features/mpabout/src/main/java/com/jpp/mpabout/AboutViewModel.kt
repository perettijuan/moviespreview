package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpabout.AboutInteractor.AboutEvent.*
import com.jpp.mpdomain.AboutUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the about section. The VM retrieves
 * the data from the underlying layers using the provided [AboutInteractor] and maps the business
 * data to UI data, producing a [AboutViewState] that represents the configuration of the view.
 */
class AboutViewModel @Inject constructor(coroutineDispatchers: CoroutineDispatchers,
                                         private val aboutInteractor: AboutInteractor)
    : MPScopedViewModel(coroutineDispatchers) {

    private val _viewState = MediatorLiveData<AboutViewState>()
    val viewState: LiveData<AboutViewState> get() = _viewState

    private val _navEvents = SingleLiveEvent<AboutNavEvent>()
    val navEvents: LiveData<AboutNavEvent> get() = _navEvents

    private lateinit var selectedItem: AboutItem
    private val supportedAboutItems = listOf(
            AboutItem.RateApp,
            AboutItem.ShareApp,
            AboutItem.PrivacyPolicy,
            AboutItem.BrowseAppCode,
            AboutItem.Licenses,
            AboutItem.TheMovieDbTermsOfUse
    )

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(aboutInteractor.events) { event ->
            when (event) {
                is AppVersionEvent -> _viewState.value = AboutViewState.showContent(
                        appVersion = "v ${event.appVersion.version}",
                        aboutItems = supportedAboutItems)
                is AboutUrlEvent -> processAboutUrl(event.aboutUrl)
                is AboutWebStoreUrlEvent -> _navEvents.value = AboutNavEvent.InnerNavigation(event.aboutUrl.url)
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit() {
        _viewState.value = AboutViewState.showLoading()
        withInteractor { fetchAppVersion() }
    }

    /**
     * Called when the user has selected an item from the about items section.
     */
    fun onUserSelectedAboutItem(aboutItem: AboutItem) {
        selectedItem = aboutItem
        when (aboutItem) {
            is AboutItem.BrowseAppCode -> withInteractor { getRepoUrl() }
            is AboutItem.TheMovieDbTermsOfUse -> withInteractor { getApiTermOfUseUrl() }
            is AboutItem.PrivacyPolicy -> withInteractor { getPrivacyPolicyUrl() }
            is AboutItem.RateApp -> withInteractor { getStoreUrl() }
            is AboutItem.ShareApp -> withInteractor { getShareUrl() }
            is AboutItem.Licenses -> _navEvents.value = AboutNavEvent.GoToLicenses
        }
    }

    /**
     * Called when the app fails to expanded the Google Play app in the device.
     */
    fun onFailedToOpenPlayStore() {
        withInteractor { getWebStoreUrl() }
    }

    private fun withInteractor(action: AboutInteractor.() -> Unit) {
        launch { withContext(Dispatchers.Default) { action(aboutInteractor) } }
    }

    private fun processAboutUrl(aboutUrl: AboutUrl) {
        _navEvents.value = when (selectedItem) {
            is AboutItem.BrowseAppCode -> AboutNavEvent.InnerNavigation(aboutUrl.url)
            is AboutItem.TheMovieDbTermsOfUse -> AboutNavEvent.InnerNavigation(aboutUrl.url)
            is AboutItem.PrivacyPolicy -> AboutNavEvent.OuterNavigation(aboutUrl.url)
            is AboutItem.RateApp -> AboutNavEvent.OpenGooglePlay(aboutUrl.url)
            is AboutItem.ShareApp -> AboutNavEvent.OpenSharing(aboutUrl.url)
            is AboutItem.Licenses -> AboutNavEvent.GoToLicenses
        }
    }
}
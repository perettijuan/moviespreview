package com.jpp.moviespreview.screens.main.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.moviespreview.screens.SingleLiveEvent
import com.jpp.mpdomain.usecase.about.AboutNavigationType
import com.jpp.mpdomain.usecase.about.GetAboutNavigationUrlUseCase
import com.jpp.mpdomain.usecase.appversion.GetAppVersionUseCase
import javax.inject.Inject

/**
 * Special type of [ViewModel] to support the about section.
 * This [ViewModel] does not supports background execution due to the nature of the about section (the
 * about section only supports navigation to other screens).
 *
 * Note: Untested for simplicity.
 */
class AboutViewModel @Inject constructor(private val appVersionUseCase: GetAppVersionUseCase,
                                         private val getAboutNavigationUrlUseCase: GetAboutNavigationUrlUseCase) : ViewModel() {

    private val viewStateLiveData by lazy { MutableLiveData<AboutViewState>() }
    private val navigationEvents by lazy { SingleLiveEvent<AboutNavEvent>() }
    private val supportedAboutItems by lazy {
        listOf(
                AboutItem.RateApp,
                AboutItem.ShareApp,
                AboutItem.PrivacyPolicy,
                AboutItem.BrowseAppCode,
                AboutItem.Licenses,
                AboutItem.TheMovieDbTermsOfUse
        )
    }

    /**
     * Called on initialization of the AboutFragment.
     */
    fun init() {
        appVersionUseCase
                .getCurrentAppVersion()
                .run {
                    viewStateLiveData.value = AboutViewState.InitialContent(
                            appVersion = "v $this",
                            aboutItems = supportedAboutItems)
                }
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [AboutViewState].
     */
    fun viewState(): LiveData<AboutViewState> = viewStateLiveData

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<AboutNavEvent> = navigationEvents

    /**
     * Called when the user has selected an item from the about items section.
     */
    fun onUserSelectedAboutItem(aboutItem: AboutItem) {
        navigationEvents.value = when (aboutItem) {
            is AboutItem.BrowseAppCode -> AboutNavEvent.InnerNavigation(getAboutNavigationUrlUseCase.getUrlFor(AboutNavigationType.AppCodeRepo))
            is AboutItem.TheMovieDbTermsOfUse -> AboutNavEvent.InnerNavigation(getAboutNavigationUrlUseCase.getUrlFor(AboutNavigationType.TheMovieDbTermsOfUse))
            is AboutItem.PrivacyPolicy -> AboutNavEvent.OuterNavigation(getAboutNavigationUrlUseCase.getUrlFor(AboutNavigationType.PrivacyPolicy))
            is AboutItem.RateApp -> AboutNavEvent.OpenGooglePlay(getAboutNavigationUrlUseCase.getUrlFor(AboutNavigationType.GooglePlayApp))
            is AboutItem.ShareApp -> AboutNavEvent.OpenSharing(getAboutNavigationUrlUseCase.getUrlFor(AboutNavigationType.ShareApp))
            is AboutItem.Licenses -> AboutNavEvent.GoToLicenses
        }
    }

    /**
     * Called when the app fails to open the Google Play app in the device.
     */
    fun onFailedToOpenPlayStore() {
        navigationEvents.value = AboutNavEvent.InnerNavigation(getAboutNavigationUrlUseCase.getUrlFor(AboutNavigationType.GooglePlayWeb))
    }

}
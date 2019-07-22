package com.jpp.mp.screens.main.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import javax.inject.Inject

/**
 * Special type of [ViewModel] to support the about section.
 * This [ViewModel] does not supports background execution due to the nature of the about section (the
 * about section only supports navigation to other screens).
 *
 * Note: Untested for simplicity.
 */
//TODO DELETE ME
class AboutViewModelDeprecated @Inject constructor() : ViewModel() {

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
     * Called on initialization of the AboutFragmentDeprecated.
     */
    fun init() {
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

    }

    /**
     * Called when the app fails to expanded the Google Play app in the device.
     */
    fun onFailedToOpenPlayStore() {

    }

}
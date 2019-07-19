package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import javax.inject.Inject
import com.jpp.mpabout.AboutInteractor.AboutEvent.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutViewModel @Inject constructor(coroutineDispatchers: CoroutineDispatchers,
                                         private val aboutInteractor: AboutInteractor)
    : MPScopedViewModel(coroutineDispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<AboutViewState>>() }
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

    init {
        _viewStates.addSource(aboutInteractor.events) { event ->
            when (event) {
                is AppVersionEvent -> _viewStates.value = of(AboutViewState.showContent(
                        appVersion = "v ${event.appVersion.version}",
                        aboutItems = supportedAboutItems))
                //TODO JPP AppLanguageChanged and AboutUrlEvent
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit() {
        _viewStates.value = of(AboutViewState.showLoading())
        withInteractor { fetchAppVersion() }
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<AboutViewState>> get() = _viewStates

    private fun withInteractor(action: AboutInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(aboutInteractor) } }
    }
}
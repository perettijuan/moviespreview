package com.jpp.moviespreview.screens.main.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jpp.mpdomain.repository.AppVersionRepository
import javax.inject.Inject

class AboutViewModel @Inject constructor(private val appVersionRepository: AppVersionRepository) : ViewModel() {

    private val viewStateLiveData by lazy { MutableLiveData<AboutViewState>() }
    private val supportedAboutItems by lazy {
        listOf(
                AboutItem.RateApp,
                AboutItem.ShareApp,
                AboutItem.BrowseAppCode,
                AboutItem.Licenses,
                AboutItem.TheMovieDbTermsOfUse
        )
    }

    /**
     * Called on initialization of the AboutFragment.
     */
    fun init() {
        appVersionRepository
                .getCurrentAppVersion()
                .run {
                    viewStateLiveData.postValue(
                            AboutViewState.InitialContent(
                                    appVersion = this,
                                    aboutItems = supportedAboutItems)
                    )
                }
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [AboutViewState].
     */
    fun viewState(): LiveData<AboutViewState> = viewStateLiveData

}
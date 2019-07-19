package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpdomain.AboutUrl
import com.jpp.mpdomain.AppVersion
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository
import com.jpp.mpdomain.repository.LanguageRepository
import javax.inject.Inject
import com.jpp.mpabout.AboutInteractor.AboutEvent.*
import javax.inject.Singleton

/**
 * Interactor to support the about section. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
class AboutInteractor @Inject constructor(private val appVersionRepository: AppVersionRepository,
                                          private val aboutUrlRepository: AboutUrlRepository) {

    sealed class AboutEvent {
        data class AppVersionEvent(val appVersion: AppVersion) : AboutEvent()
        data class AboutUrlEvent(val aboutUrl: AboutUrl) : AboutEvent()
        data class AboutWebStoreUrlEvent(val aboutUrl: AboutUrl) : AboutEvent()
    }

    private val _events by lazy { MediatorLiveData<AboutEvent>() }

    /**
     * @return a [LiveData] of [AboutEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val events: LiveData<AboutEvent> get() = _events

    /**
     * Fetches the current version of the application. It will post a new event
     * to [events] with the version retrieved.
     */
    fun fetchAppVersion() {
        _events.postValue(AppVersionEvent(appVersionRepository.getCurrentAppVersion()))
    }

    fun getApiTermOfUseUrl() {
        _events.postValue(AboutUrlEvent(aboutUrlRepository.getTheMovieDbTermOfUseUrl()))
    }

    fun getRepoUrl() {
        _events.postValue(AboutUrlEvent(aboutUrlRepository.getCodeRepoUrl()))
    }

    fun getStoreUrl() {
        _events.postValue(AboutUrlEvent(aboutUrlRepository.getGPlayAppUrl()))
    }

    fun getWebStoreUrl() {
        _events.postValue(AboutWebStoreUrlEvent(aboutUrlRepository.getGPlayWebUrl()))
    }

    fun getShareUrl() {
        _events.postValue(AboutUrlEvent(aboutUrlRepository.getSharingUrl()))
    }

    fun getPrivacyPolicyUrl() {
        _events.postValue(AboutUrlEvent(aboutUrlRepository.getPrivacyPolicyUrl()))
    }

}
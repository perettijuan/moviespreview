package com.jpp.mpabout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpabout.AboutInteractor.AboutEvent.*
import com.jpp.mpdomain.AboutUrl
import com.jpp.mpdomain.AppVersion
import com.jpp.mpdomain.Licenses
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AppVersionRepository
import com.jpp.mpdomain.repository.LicensesRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interactor to support the about section. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
class AboutInteractor @Inject constructor(private val appVersionRepository: AppVersionRepository,
                                          private val aboutUrlRepository: AboutUrlRepository,
                                          private val licensesRepository: LicensesRepository) {

    sealed class AboutEvent {
        data class AppVersionEvent(val appVersion: AppVersion) : AboutEvent()
        data class AboutUrlEvent(val aboutUrl: AboutUrl) : AboutEvent()
        data class AboutWebStoreUrlEvent(val aboutUrl: AboutUrl) : AboutEvent()
    }

    sealed class LicensesEvent {
        object UnknownError : LicensesEvent()
        data class Sucess(val results: Licenses) : LicensesEvent()
    }

    private val _events by lazy { MediatorLiveData<AboutEvent>() }
    private val _licenseEvents by lazy { MediatorLiveData<LicensesEvent>() }

    /**
     * @return a [LiveData] of [AboutEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val events: LiveData<AboutEvent> get() = _events

    /**
     * @return a [LiveData] of [AboutEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val licenseEvents: LiveData<LicensesEvent> get() = _licenseEvents

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

    fun fetchAppLicenses() {
        when (val licenses = licensesRepository.loadLicences()) {
            null -> LicensesEvent.UnknownError
            else -> LicensesEvent.Sucess(licenses)
        }.let {
            _licenseEvents.postValue(it)
        }
    }

}
package com.jpp.mpcredits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpcredits.CreditsInteractor.CreditsEvent.*
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import com.jpp.mpdomain.repository.LanguageRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interactor to support the credits section. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
class CreditsInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                            private val creditsRepository: CreditsRepository,
                                            languageRepository: LanguageRepository) {


    sealed class CreditsEvent {
        object AppLanguageChanged : CreditsEvent()
        object NotConnectedToNetwork : CreditsEvent()
        object UnknownError : CreditsEvent()
        data class Success(val credits: Credits) : CreditsEvent()
    }

    private val _events = MediatorLiveData<CreditsEvent>()

    init {
        _events.addSource(languageRepository.updates()) {
            _events.postValue(AppLanguageChanged)
        }
    }

    /**
     * @return a [LiveData] of [CreditsEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val events: LiveData<CreditsEvent> get() = _events

    /**
     * Fetches the [Credits] list that corresponds to the movie identified by [movieId].
     * It will post a new event to [events] indicating the result of the action.
     */
    fun fetchCreditsForMovie(movieId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> NotConnectedToNetwork
            is Connectivity.Connected -> {
                creditsRepository
                        .getCreditsForMovie(movieId)?.let { Success(it) } ?: UnknownError
            }
        }.let { _events.postValue(it) }
    }

    /**
     * Flushes out any credits stored data.
     */
    fun flushCreditsData() {
        creditsRepository.flushCreditsData()
    }
}
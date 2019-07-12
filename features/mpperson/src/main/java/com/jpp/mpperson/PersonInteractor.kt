package com.jpp.mpperson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.PersonRepository
import com.jpp.mpperson.PersonInteractor.PersonEvent.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interactor to support the person screen. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * view layer.
 */
@Singleton
class PersonInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                           private val personRepository: PersonRepository,
                                           private val languageRepository: LanguageRepository) {

    sealed class PersonEvent {
        object AppLanguageChanged : PersonEvent()
        object NotConnectedToNetwork : PersonEvent()
        object UnknownError : PersonEvent()
        data class Success(val person: Person) : PersonEvent()
    }

    private val _events by lazy { MediatorLiveData<PersonEvent>() }

    init {
        _events.addSource(languageRepository.updates()) {
            _events.postValue(AppLanguageChanged)
        }
    }

    /**
     * @return a [LiveData] of [PersonEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val events: LiveData<PersonEvent> get() = _events

    /**
     * Fetches the [Person] that corresponds to the provided by [personId].
     * It will post a new event to [events] indicating the result of the action.
     */
    fun fetchPerson(personId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> _events.postValue(NotConnectedToNetwork)
            is Connectivity.Connected -> {
                personRepository
                        .getPerson(personId, languageRepository.getCurrentAppLanguage())
                        ?.let { _events.postValue(Success(it)) }
                        ?: _events.postValue(UnknownError)
            }
        }
    }

    /**
     * Flushes out any person stored data.
     */
    fun flushPersonData() {
        personRepository.flushPersonData()
    }
}
package com.jpp.mpperson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.PersonRepository
import com.jpp.mpperson.PersonInteractor.PersonEvent.*
import javax.inject.Inject
import javax.inject.Singleton

//TODO JPP language support.
@Singleton
class PersonInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                           private val personRepository: PersonRepository,
                                           private val languageRepository: LanguageRepository) {

    sealed class PersonEvent {
        object NotConnectedToNetwork : PersonEvent()
        object UnknownError : PersonEvent()
        data class Success(val person: Person) : PersonEvent()
    }

    private val _personEvents by lazy { MutableLiveData<PersonEvent>() }

    /**
     * @return a [LiveData] of [PersonEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val events: LiveData<PersonEvent> get() = _personEvents

    /**
     * Fetches the [Person] that corresponds to the provided by [personId].
     * It will post a new event to [events] indicating the result of the action.
     */
    fun fetchPerson(personId: Double) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> _personEvents.postValue(NotConnectedToNetwork)
            is Connectivity.Connected -> {
                personRepository
                        .getPerson(personId, languageRepository.getCurrentAppLanguage())
                        ?.let { _personEvents.postValue(Success(it)) }
                        ?: _personEvents.postValue(UnknownError)
            }
        }
    }
}
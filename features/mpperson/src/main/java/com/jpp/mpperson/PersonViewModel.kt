package com.jpp.mpperson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.Person
import com.jpp.mpperson.PersonErrorState.Companion.asConnectivity
import com.jpp.mpperson.PersonErrorState.Companion.asUnknownError
import com.jpp.mpperson.PersonInteractor.PersonEvent.*
import com.jpp.mpperson.PersonViewState.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PersonViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val personInteractor: PersonInteractor)
    : MPScopedViewModel(dispatchers) {

    private val retry = { executeFetchPersonStep(personId) }
    private val _viewStates by lazy { MediatorLiveData<HandledViewState<PersonViewState>>() }
    private var personId: Double = 0.0

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(personInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowError(asConnectivity(retry)))
                is UnknownError -> _viewStates.value = of(ShowError(asUnknownError(retry)))
                is Success -> _viewStates.value = of(getViewStateFromPerson(event.person))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(personId: Double) {
        this.personId = personId
        executeFetchPersonStep(personId)
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<PersonViewState>> get() = _viewStates


    private fun executeFetchPersonStep(personId: Double) {
        launch { withContext(dispatchers.default()) { personInteractor.fetchPerson(personId) } }
        _viewStates.value = of(ShowLoading)
    }

    private fun getViewStateFromPerson(person: Person): PersonViewState {
        return when (person.isEmpty()) {
            true -> ShowNoDataAvailable
            else -> ShowPerson(contentValue = mapPersonData(person))
        }
    }

    private fun mapPersonData(person: Person): PersonContent {
        return PersonContent(
                birthday = person.birthday?.let { PersonRow.Birthday(it) } ?: PersonRow.EmptyRow,
                placeOfBirth = person.place_of_birth?.let { PersonRow.PlaceOfBirth(it) } ?: PersonRow.EmptyRow,
                deathDay = person.deathday?.let { PersonRow.DeathDay(it) } ?: PersonRow.EmptyRow,
                bio = if (person.biography.isEmpty()) PersonRow.EmptyRow else PersonRow.Bio(person.biography)
        )
    }

    private fun Person.isEmpty() = biography.isEmpty()
            && birthday.isNullOrEmpty()
            && deathday.isNullOrEmpty()
            && place_of_birth.isNullOrEmpty()
}
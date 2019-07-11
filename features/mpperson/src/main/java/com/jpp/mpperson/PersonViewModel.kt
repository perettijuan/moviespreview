package com.jpp.mpperson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.Person
import com.jpp.mpperson.PersonInteractor.PersonEvent.*
import com.jpp.mpperson.PersonViewState.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PersonViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val personInteractor: PersonInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<PersonViewState>>() }
    private var personId: Double = 0.0

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(personInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowNoConnectivity)
                is UnknownError -> _viewStates.value = of(ShowUnknownError)
                is Success -> _viewStates.value = of(ShowPerson(mapPersonData(event.person)))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(personId: Double) {
        this.personId = personId
        _viewStates.value = of(executeFetchPersonStep(personId))
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<PersonViewState>> get() = _viewStates

    private fun withInteractor(action: PersonInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(personInteractor) } }
    }

    private fun executeFetchPersonStep(personId: Double): PersonViewState {
        withInteractor { fetchPerson(personId) }
        return ShowLoading
    }

    private fun mapPersonData(person: Person): PersonContent {
        return PersonContent(
                birthday = person.birthday?.let { UIPersonRow.Birthday(it) } ?: UIPersonRow.NoDataAvailable,
                placeOfBirth = person.place_of_birth?.let { UIPersonRow.PlaceOfBirth(it) } ?: UIPersonRow.NoDataAvailable,
                deathDay = person.deathday?.let { UIPersonRow.DeathDay(it) } ?: UIPersonRow.NoDataAvailable,
                bio = if (person.biography.isEmpty()) UIPersonRow.NoDataAvailable else UIPersonRow.Bio(person.biography)
        )
    }


}
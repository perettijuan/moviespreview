package com.jpp.mpperson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.Person
import com.jpp.mpperson.PersonInteractor.PersonEvent.*
import com.jpp.mpperson.PersonRowViewState.Companion.bioRow
import com.jpp.mpperson.PersonRowViewState.Companion.birthdayRow
import com.jpp.mpperson.PersonRowViewState.Companion.deathDayRow
import com.jpp.mpperson.PersonRowViewState.Companion.emptyRow
import com.jpp.mpperson.PersonRowViewState.Companion.placeOfBirthRow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PersonViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val personInteractor: PersonInteractor)
    : MPScopedViewModel(dispatchers) {

    private val retry: () -> Unit = { executeFetchPersonStep(personId) }
    private val _viewStates by lazy { MediatorLiveData<HandledViewState<PersonViewState>>() }
    private var personId: Double = 0.0

    init {
        _viewStates.addSource(personInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> of(PersonViewState.showNoConnectivityError(retry))
                is UnknownError -> of(PersonViewState.showUnknownError(retry))
                is Success -> of(getViewStateFromPerson(event.person))
                is AppLanguageChanged -> of(executeRefreshDataStep(personId))
            }.let {
                _viewStates.value = it
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


    private fun executeFetchPersonStep(personId: Double): PersonViewState {
        withInteractor { fetchPerson(personId) }
        return PersonViewState.showLoading()
    }

    private fun executeRefreshDataStep(personId: Double): PersonViewState {
        withInteractor {
            flushPersonData()
            fetchPerson(personId)
        }
        return PersonViewState.showLoading()
    }

    private fun withInteractor(action: PersonInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(personInteractor) } }
    }

    private fun getViewStateFromPerson(person: Person): PersonViewState {
        return when (person.isEmpty()) {
            true -> PersonViewState.showNoDataAvailable()
            else -> PersonViewState.showPerson(mapPersonData(person))
        }
    }

    private fun mapPersonData(person: Person): PersonContentViewState {
        return PersonContentViewState(
                birthday = person.birthday?.let { birthdayRow(it) } ?: emptyRow(),
                placeOfBirth = person.place_of_birth?.let { placeOfBirthRow(it) } ?: emptyRow(),
                deathDay = person.deathday?.let { deathDayRow(it) } ?: emptyRow(),
                bio = if (person.biography.isEmpty()) emptyRow() else bioRow(person.biography)
        )
    }

    private fun Person.isEmpty() = biography.isEmpty()
            && birthday.isNullOrEmpty()
            && deathday.isNullOrEmpty()
            && place_of_birth.isNullOrEmpty()
}
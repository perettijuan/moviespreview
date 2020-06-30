package com.jpp.mpperson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.usecase.GetPersonUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mpperson.PersonRowViewState.Companion.bioRow
import com.jpp.mpperson.PersonRowViewState.Companion.birthdayRow
import com.jpp.mpperson.PersonRowViewState.Companion.deathDayRow
import com.jpp.mpperson.PersonRowViewState.Companion.emptyRow
import com.jpp.mpperson.PersonRowViewState.Companion.placeOfBirthRow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the person section. The VM retrieves
 * the data from the underlying layers and maps the business
 * data to UI data, producing a [PersonViewState] that represents the configuration of the view.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class PersonViewModel(
    private val getPersonUseCase: GetPersonUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewState = MutableLiveData<PersonViewState>()
    internal val viewState: LiveData<PersonViewState> = _viewState

    private var personId: Double
        set(value) = savedStateHandle.set(PERSON_ID_KEY, value)
        get() = savedStateHandle.get(PERSON_ID_KEY)
            ?: throw IllegalStateException("Trying to access $PERSON_ID_KEY when it is not yet set")

    private var personName: String
        set(value) = savedStateHandle.set(PERSON_NAME_KEY, value)
        get() = savedStateHandle.get(PERSON_NAME_KEY)
            ?: throw IllegalStateException("Trying to access $PERSON_NAME_KEY when it is not yet set")

    private var personImageUrl: String
        set(value) = savedStateHandle.set(PERSON_IMAGE_URL_KEY, value)
        get() = savedStateHandle.get(PERSON_IMAGE_URL_KEY)
            ?: throw IllegalStateException("Trying to access $PERSON_IMAGE_URL_KEY when it is not yet set")

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(param: PersonParam) {
        personId = param.personId
        personName = param.personName
        personImageUrl = param.imageUrl
        fetchPersonData()
    }

    private fun fetchPersonData() {
        _viewState.value =
            PersonViewState.showLoading(personName, personImageUrl)
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getPersonUseCase.execute(personId)
            }

            when (result) {
                is Try.Failure -> processFailure(result.cause)
                is Try.Success -> processPersonData(result.value)
            }
        }
    }

    private fun processFailure(cause: Try.FailureCause) {
        _viewState.value = when (cause) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError { fetchPersonData() }
            else -> _viewState.value?.showUnknownError { fetchPersonData() }
        }
    }

    private fun processPersonData(person: Person) {
        _viewState.value = when (person.isEmpty()) {
            true -> _viewState.value?.showNoDataAvailable(personImageUrl)
            else -> _viewState.value?.showPerson(personImageUrl, person.mapToViewState())
        }
    }

    private fun Person.mapToViewState(): PersonContentViewState {
        return PersonContentViewState(
            birthday = birthday?.let { birthdayRow(it) } ?: emptyRow(),
            placeOfBirth = place_of_birth?.let { placeOfBirthRow(it) } ?: emptyRow(),
            deathDay = deathday?.let { deathDayRow(it) } ?: emptyRow(),
            bio = if (biography.isEmpty()) emptyRow() else bioRow(biography)
        )
    }

    private fun Person.isEmpty() = biography.isEmpty() &&
            birthday.isNullOrEmpty() &&
            deathday.isNullOrEmpty() &&
            place_of_birth.isNullOrEmpty()

    private companion object {
        const val PERSON_ID_KEY = "PERSON_ID_KEY"
        const val PERSON_NAME_KEY = "PERSON_NAME_KEY"
        const val PERSON_IMAGE_URL_KEY = "PERSON_IMAGE_URL_KEY"
    }
}

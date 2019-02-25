package com.jpp.moviespreview.screens.main.person

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.moviespreview.screens.CoroutineDispatchers
import com.jpp.moviespreview.screens.MPScopedViewModel
import com.jpp.mpdomain.Person
import com.jpp.mpdomain.usecase.person.GetPersonUseCase
import com.jpp.mpdomain.usecase.person.GetPersonUseCaseResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * TODO JPP verifica si te conviene tener un UseCase para crear el image path de person.
 */
class PersonViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                          private val getPersonUseCase: GetPersonUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<PersonViewState>() }
    private lateinit var retryFunc: () -> Unit


    fun init(personId: Double, personImageUrl: String, personName: String) {
        retryFunc = { pushLoadingAndFetchPersonInfo(personId, personImageUrl, personName) }
        pushLoadingAndFetchPersonInfo(personId, personImageUrl, personName)
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [PersonViewState].
     */
    fun viewState(): LiveData<PersonViewState> = viewStateLiveData

    /**
     * Called in order to execute the last attempt to fetch a person's details.
     */
    fun retry() {
        when (viewStateLiveData.value) {
            is PersonViewState.ErrorNoConnectivity -> retryFunc.invoke()
            is PersonViewState.ErrorUnknown -> retryFunc.invoke()
        }
    }

    /**
     * Pushes the loading state to the view and launches the process to fetch the person's data.
     */
    private fun pushLoadingAndFetchPersonInfo(personId: Double, personImageUrl: String, personName: String) {
        viewStateLiveData.value = PersonViewState.Loading(imageUrl = personImageUrl, name = personName)
        launch {
            /*
             * This work is being executed in the default dispatcher, which indicates that is
             * running in a different thread that the UI thread.
             * Since the default context in ViewModel is the main context (UI thread), once
             * that withContext returns is value, we're back in the main context.
             */
            viewStateLiveData.value = fetchPerson(personId)
        }
    }

    /**
     * Fetches the person that is identified by [personId]
     * @return the [PersonViewState] that will be rendered as result of the
     * use case execution.
     */
    private suspend fun fetchPerson(personId: Double): PersonViewState = withContext(dispatchers.default()) {
        getPersonUseCase
                .getPerson(personId)
                .let { ucResult ->
                    when (ucResult) {
                        is GetPersonUseCaseResult.ErrorNoConnectivity -> PersonViewState.ErrorNoConnectivity
                        is GetPersonUseCaseResult.ErrorUnknown -> PersonViewState.ErrorUnknown
                        is GetPersonUseCaseResult.Success -> {
                            if (isPersonDataEmpty(ucResult.person)) {
                                PersonViewState.LoadedEmpty
                            } else {
                                PersonViewState.Loaded(
                                        person = mapPersonToUiPerson(ucResult.person),
                                        showBirthday = ucResult.person.birthday != null,
                                        showDeathDay = ucResult.person.deathday != null,
                                        showPlaceOfBirth = ucResult.person.place_of_birth != null
                                )
                            }
                        }
                    }
                }
    }

    private fun isPersonDataEmpty(person: Person) = person.biography.isEmpty()
            && person.birthday.isNullOrEmpty()
            && person.deathday.isNullOrEmpty()
            && person.place_of_birth.isNullOrEmpty()


    /**
     * Maps a domain [Person] into a [UiPerson] ready to be rendered.
     */
    private fun mapPersonToUiPerson(domainPerson: Person): UiPerson =
            with(domainPerson) {
                UiPerson(
                        name = name,
                        biography = biography,
                        birthday = birthday ?: "",
                        deathday = deathday ?: "",
                        placeOfBirth = place_of_birth ?: ""
                )
            }
}
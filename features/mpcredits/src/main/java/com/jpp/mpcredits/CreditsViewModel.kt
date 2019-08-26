package com.jpp.mpcredits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.extensions.addAllMapping
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpcredits.CreditsInteractor.CreditsEvent.*
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the credits section. The VM retrieves
 * the data from the underlying layers using the provided [CreditsInteractor] and maps the business
 * data to UI data, producing a [CreditsViewState] that represents the configuration of the view
 * at any given moment. The mapping process involves fetching and parsing the paths for the images
 * that each credit possess. This mapping process is executed using the provided [ImagesPathInteractor]
 * to perform the mention images path configuration.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class CreditsViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                           private val creditsInteractor: CreditsInteractor,
                                           private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<CreditsViewState>>() }
    val viewStates: LiveData<HandledViewState<CreditsViewState>> get() = _viewStates

    private val _navEvents by lazy { SingleLiveEvent<NavigateToPersonEvent>() }
    val navEvents: LiveData<NavigateToPersonEvent> get() = _navEvents

    private lateinit var currentParam: CreditsInitParam

    private val retry: () -> Unit = { fetchMovieCredits(currentParam.movieId) }

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(creditsInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(CreditsViewState.showNoConnectivityError(retry))
                is UnknownError -> _viewStates.value = of(CreditsViewState.showUnknownError(retry))
                is Success -> mapCreditsAndPushViewState(event.credits)
                is AppLanguageChanged -> refreshCreditsData(currentParam.movieId)
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: CreditsInitParam) {
        currentParam = param
        when (val currentState = _viewStates.value) {
            null -> fetchMovieCredits(currentParam.movieId)
            else -> _viewStates.value = of(currentState.peekContent())
        }
    }

    /**
     * Called when an item is selected in the list of credit persons.
     * A new state is posted in [navEvents] in order to handle the event.
     */
    fun onCreditItemSelected(personItem: CreditPerson) {
        with(personItem) {
            _navEvents.value = NavigateToPersonEvent(
                    personId = id.toString(),
                    personImageUrl = profilePath,
                    personName = subTitle
            )
        }
    }

    /**
     * When called, this method will push the loading view state and will fetch the credits
     * of the movie being shown. When the fetching process is done, the view state will be updated
     * based on the result posted by the interactor.
     */
    private fun fetchMovieCredits(movieId: Double) {
        withInteractor { fetchCreditsForMovie(movieId) }
        _viewStates.value = of(CreditsViewState.showLoading())
    }

    /**
     * Starts a refresh data process by indicating to the interactor that new data needs
     * to be fetched for the credits being shown. This is executed in a background
     * task while the view state is updated with the loading state.
     */
    private fun refreshCreditsData(creditsId: Double) {
        withInteractor {
            flushCreditsData()
            fetchCreditsForMovie(creditsId)
        }
        _viewStates.value = of(CreditsViewState.showLoading())
    }

    /**
     * Helper function to execute an [action] in the [creditsInteractor] instance
     * on a background task.
     */
    private fun withInteractor(action: CreditsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(creditsInteractor) } }
    }

    /**
     * Maps the [credits] provided from the domain layer to the view layer producing
     * a new view state that is pushed to the view to show the result of the credits
     * fetching process.
     */
    private fun mapCreditsAndPushViewState(credits: Credits) {
        when (credits.cast.isEmpty() && credits.crew.isEmpty()) {
            true -> _viewStates.value = of(CreditsViewState.showNoCreditsAvailable())
            false -> {
                launch {
                    withContext(dispatchers.default()) {
                        CreditsViewState.showCredits(
                                credits.cast
                                        .map { imagesPathInteractor.configureCastCharacter(currentParam.targetImageSize, it) }
                                        .map { mapCastCharacterToCreditPerson(it) }
                                        .toMutableList()
                                        .addAllMapping { credits.crew.map { crewMember -> mapCrewMemberToCreditPerson(crewMember) } }
                        )
                    }.let { _viewStates.value = of(it) }
                }
            }
        }
    }

    /**
     * Map a [CastCharacter] to a [CreditPerson].
     */
    private fun mapCastCharacterToCreditPerson(castCharacter: CastCharacter): CreditPerson =
            with(castCharacter) {
                CreditPerson(
                        id = id,
                        profilePath = profile_path ?: "empty",
                        title = character,
                        subTitle = name
                )
            }

    /**
     * Map a [CrewMember] to a [CreditPerson].
     */
    private fun mapCrewMemberToCreditPerson(crewMember: CrewMember): CreditPerson =
            with(crewMember) {
                CreditPerson(
                        id = id,
                        profilePath = profile_path ?: "empty",
                        title = name,
                        subTitle = department
                )
            }
}
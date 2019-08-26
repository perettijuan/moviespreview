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

class CreditsViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                           private val creditsInteractor: CreditsInteractor,
                                           private val imagesPathInteractor: ImagesPathInteractor)
    : MPScopedViewModel(dispatchers) {


    private val retry: () -> Unit = { executeFetchCreditsStep(movieId) }
    private var movieId: Double = 0.0
    private var targetImageSize: Int = -1
    private val _viewStates by lazy { MediatorLiveData<HandledViewState<CreditsViewState>>() }
    private val _navEvents by lazy { SingleLiveEvent<CreditsNavigationEvent>() }

    init {
        _viewStates.addSource(creditsInteractor.events) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(CreditsViewState.showNoConnectivityError(retry))
                is UnknownError -> _viewStates.value = of(CreditsViewState.showUnknownError(retry))
                is Success -> mapCreditsAndPushViewState(event.credits)
                is AppLanguageChanged -> _viewStates.value = of(executeRefreshDataStep(movieId))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(movieId: Double, targetImageSize: Int) {
        this.movieId = movieId
        this.targetImageSize = targetImageSize
        when (val currentState = _viewStates.value) {
            null ->_viewStates.value = of(executeFetchCreditsStep(movieId))
            else -> _viewStates.value = of(currentState.peekContent())
        }
    }

    /**
     * Called when an item is selected in the list of credit persons.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onCreditItemSelected(personItem: CreditPerson) {
        with (personItem) {
            _navEvents.value = CreditsNavigationEvent.ToPerson(personId = id.toString(), personImageUrl = profilePath, personName = subTitle)
        }
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<CreditsViewState>> get() = _viewStates

    /**
     * Subscribe to this [LiveData] in order to get notified about navigation steps that
     * should be performed by the view.
     */
    val navEvents: LiveData<CreditsNavigationEvent> get() = _navEvents

    private fun executeFetchCreditsStep(movieId: Double): CreditsViewState {
        withInteractor { fetchCreditsForMovie(movieId) }
        return CreditsViewState.showLoading()
    }

    private fun executeRefreshDataStep(creditsId: Double): CreditsViewState {
        withInteractor {
            flushCreditsData()
            fetchCreditsForMovie(creditsId)
        }
        return CreditsViewState.showLoading()
    }

    private fun withInteractor(action: CreditsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(creditsInteractor) } }
    }

    private fun mapCreditsAndPushViewState(credits: Credits) {
        when (credits.cast.isEmpty() && credits.crew.isEmpty()) {
            true -> _viewStates.value = of(CreditsViewState.showNoCreditsAvailable())
            false -> {
                launch {
                    withContext(dispatchers.default()) {
                        CreditsViewState.showCredits(
                                credits.cast
                                        .map { imagesPathInteractor.configureCastCharacter(targetImageSize, it) }
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
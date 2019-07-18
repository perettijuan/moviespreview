package com.jpp.mp.screens.main.credits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.CrewMember
import javax.inject.Inject

/**
 * [MPScopedViewModel] to handle the state of the CreditsFragmentDeprecated. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It exposes a single output in a LiveData object that receives [CreditsViewState] updates as soon
 * as any new state is identified by the ViewModel.
 */
//TODO JPP delete ME
class CreditsViewModelDeprecated @Inject constructor(dispatchers: CoroutineDispatchers)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<CreditsViewState>() }
    private val navigationEvents by lazy { SingleLiveEvent<CreditsNavigationEvent>() }
    private lateinit var retryFunc: () -> Unit

    /**
     * Called on initialization of the CreditsFragmentDeprecated.
     * Each time this method is called the backing UseCase are executed in order to retrieve
     * the credits of the movie identified by [movieId].
     * The updates will be posted to the [LiveData] object provided by [viewState()].
     */
    fun init(movieId: Double, targetImageSize: Int) {
        retryFunc = { pushLoadingAndFetchMovieCredits(movieId, targetImageSize) }
        pushLoadingAndFetchMovieCredits(movieId, targetImageSize)
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [CreditsViewState].
     */
    fun viewState(): LiveData<CreditsViewState> = viewStateLiveData

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<CreditsNavigationEvent> = navigationEvents

    /**
     * Called when an item is selected in the list of credit persons.
     * A new state is posted in navEvents() in order to handle the event.
     */
    fun onCreditItemSelected(personItem: CreditPerson) {
        with(personItem) {
            navigationEvents.value = CreditsNavigationEvent.ToPerson(personId = id.toString(), personImageUrl = profilePath, personName = subTitle)
        }
    }

    /**
     * Called in order to execute the last attempt to fetch the credits.
     */
    fun retry() {
        when (viewStateLiveData.value) {
            CreditsViewState.ErrorUnknown -> retryFunc.invoke()
            CreditsViewState.ErrorNoConnectivity -> retryFunc.invoke()
        }
    }

    /**
     * Pushes the loading state into the view and starts the process to fetch the credits
     * of the movie.
     */
    private fun pushLoadingAndFetchMovieCredits(movieId: Double, targetImageSize: Int) {
        viewStateLiveData.value = CreditsViewState.Loading
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
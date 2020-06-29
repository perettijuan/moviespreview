package com.jpp.mpcredits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.extensions.addAllMapping
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.CrewMember
import com.jpp.mpdomain.usecase.GetCreditsUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the credits section. The VM retrieves
 * the data from the underlying layers and maps the business
 * data to UI data, producing a [CreditsViewState] that represents the configuration of the view
 * at any given moment.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class CreditsViewModel(
    private val getCreditsUseCase: GetCreditsUseCase,
    private val navigator: CreditNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewState = MutableLiveData<CreditsViewState>()
    internal val viewState: LiveData<CreditsViewState> = _viewState

    private var movieId: Double
        set(value) = savedStateHandle.set(MOVIE_ID_KEY, value)
        get() = savedStateHandle.get(MOVIE_ID_KEY)
            ?: throw IllegalStateException("Trying to access $MOVIE_ID_KEY when it is not yet set")

    private var movieTitle: String
        set(value) = savedStateHandle.set(MOVIE_TITLE_KEY, value)
        get() = savedStateHandle.get(MOVIE_TITLE_KEY)
            ?: throw IllegalStateException("Trying to access $MOVIE_TITLE_KEY when it is not yet set")

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(param: CreditsInitParam) {
        movieId = param.movieId
        movieTitle = param.movieTitle
        fetchMovieCredits()
    }

    /**
     * Called when an item is selected in the list of credit persons.
     */
    internal fun onCreditItemSelected(personItem: CreditPerson) {
        navigator.navigateToCreditDetail(
            personItem.id.toString(),
            personItem.profilePath,
            personItem.subTitle
        )
    }

    /**
     * When called, this method will push the loading view state and will fetch the credits
     * of the movie being shown.
     */
    private fun fetchMovieCredits() {
        _viewState.value = CreditsViewState.showLoading(movieTitle)
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getCreditsUseCase.execute(movieId)
            }

            when (result) {
                is Try.Success -> processCreditsResponse(result.value)
                is Try.Failure -> processFailure(result.cause)
            }
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError { fetchMovieCredits() }
            else -> _viewState.value?.showUnknownError { fetchMovieCredits() }
        }
    }

    private fun processCreditsResponse(credits: Credits) {
        if (credits.cast.isEmpty() && credits.crew.isEmpty()) {
            _viewState.value = _viewState.value?.showNoCreditsAvailable()
            return
        }

        val creditPersonItems = credits.cast
            .map { castCharacter -> castCharacter.mapToCreditPerson() }
            .toMutableList()
            .addAllMapping { credits.crew.map { crewMember -> crewMember.mapToCreditPerson() } }

        _viewState.value = _viewState.value?.showCredits(creditPersonItems)
    }

    private fun CastCharacter.mapToCreditPerson(): CreditPerson {
        return CreditPerson(
            id = id,
            profilePath = profile_path ?: "empty",
            title = character,
            subTitle = name
        )
    }

    private fun CrewMember.mapToCreditPerson(): CreditPerson {
        return CreditPerson(
            id = id,
            profilePath = profile_path ?: "empty",
            title = name,
            subTitle = department
        )
    }

    private companion object {
        const val MOVIE_ID_KEY = "MOVIE_ID_KEY"
        const val MOVIE_TITLE_KEY = "MOVIE_TITLE_KEY"
    }
}

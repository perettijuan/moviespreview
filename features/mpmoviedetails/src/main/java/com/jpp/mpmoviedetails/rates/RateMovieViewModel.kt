package com.jpp.mpmoviedetails.rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.usecase.DeleteMovieRatingUseCase
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.RateMovieUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [MPViewModel] that supports the movie movie rating feature. The VM retrieves
 * the data from the underlying layers and maps the business
 * data to UI data, producing a [RateMovieViewState] that represents the configuration of the view
 * at any given moment. It also exposes message updates as [RateMovieUserMessages] in order to notify
 * particular updates.
 *
 * When the user performs the rating action the VM updates the state
 * of the movie internally and in the server side and updates the view layer according to the new
 * state of the business layer.
 */
class RateMovieViewModel(
    private val getMovieStateUseCase: GetMovieStateUseCase,
    private val rateMovieUseCase: RateMovieUseCase,
    private val deleteMovieRatingUseCase: DeleteMovieRatingUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : MPViewModel() {

    private val _viewState = MediatorLiveData<RateMovieViewState>()
    internal val viewState: LiveData<RateMovieViewState> = _viewState

    private val _userMessages = MediatorLiveData<HandledEvent<RateMovieUserMessages>>()
    internal val userMessages: LiveData<HandledEvent<RateMovieUserMessages>> = _userMessages

    private var movieId: Double
        set(value) {
            savedStateHandle.set(MOVIE_ID_KEY, value)
        }
        get() {
            return savedStateHandle.get(MOVIE_ID_KEY)
                ?: throw IllegalStateException("Trying to access MOVIE_ID_KEY when it is not yet set")
        }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(param: RateMovieParam) {
        movieId = param.movieId
        fetchMovieState(
            param.movieId,
            param.screenTitle,
            param.movieImageUrl
        )
    }

    /**
     * Rates the current movie being shown with the provided [rating].
     */
    internal fun onRateMovie(rating: Float) {
        if (_viewState.value?.rating != rating) {
            _viewState.value = _viewState.value?.updateLoading()

            viewModelScope.launch {
                val scaledRating = scaleUpRatingValue(rating)
                val result = withContext(ioDispatcher) {
                    rateMovieUseCase.execute(movieId, scaledRating)
                }

                when (result) {
                    is Try.Success -> postUserMessageAndExit(RateMovieUserMessages.RATE_SUCCESS)
                    is Try.Failure -> postUserMessageAndExit(RateMovieUserMessages.RATE_ERROR)
                }
            }
        }
    }

    /**
     * Called when the user attempts to delete a previously rating set for the movie
     * being shown.
     */
    internal fun onDeleteMovieRating() {
        _viewState.value = _viewState.value?.updateLoading()

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                deleteMovieRatingUseCase.execute(movieId)
            }

            when (result) {
                is Try.Success -> postUserMessageAndExit(RateMovieUserMessages.DELETE_SUCCESS)
                is Try.Failure -> postUserMessageAndExit(RateMovieUserMessages.DELETE_ERROR)
            }
        }
    }

    /**
     * When called, this method will push the loading view state and will fetch the movie state
     * of the movie being shown.
     */
    private fun fetchMovieState(movieId: Double, movieTitle: String, movieImageUrl: String) {
        _viewState.value = RateMovieViewState.createLoading(
            movieTitle,
            movieImageUrl
        )

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getMovieStateUseCase.execute(movieId)
            }

            when (result) {
                is Try.Success -> processMovieStateUpdate(result.value)
                is Try.Failure -> processFailure(result.cause)
            }
        }
    }


    /**
     * Process the [MovieState] provided producing a [RateMovieViewState] that contains
     * the data to be shown to the user.
     */
    private fun processMovieStateUpdate(
        movieState: MovieState
    ) {
        val rating = movieState.rated.value?.toFloat()
        _viewState.value = if (rating == null) {
            _viewState.value?.showNoRated()
        } else {
            _viewState.value?.showRated(scaleDownRatingValue(rating))
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        when (failure) {
            is Try.FailureCause.UserNotLogged -> postUserMessageAndExit(RateMovieUserMessages.USER_NOT_LOGGED)
            else -> postUserMessageAndExit(RateMovieUserMessages.ERROR_FETCHING_DATA)
        }
    }

    /**
     * Posts the provided [message] to [userMessages] and exists the current flow.
     */
    private fun postUserMessageAndExit(message: RateMovieUserMessages) {
        _userMessages.value = of(message)
        navigateTo(Destination.PreviousDestination)
    }

    /**
     * Scale the obtained rating: since the value is a 10th-based value and we're showing
     * 5 stars, we need to scale by two in order to properly set the UI.
     */
    private fun scaleDownRatingValue(value: Float): Float = value / SCALING_FACTOR

    private fun scaleUpRatingValue(value: Float): Float = value * SCALING_FACTOR

    private companion object {
        const val SCALING_FACTOR = 2
        const val MOVIE_ID_KEY = "MOVIE_ID_KEY"
    }
}

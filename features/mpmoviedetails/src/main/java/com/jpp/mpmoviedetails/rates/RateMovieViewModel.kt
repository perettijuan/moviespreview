package com.jpp.mpmoviedetails.rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.MovieState
import com.jpp.mpmoviedetails.MovieDetailsInteractor
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//TODO JPP add javadoc
class RateMovieViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                             private val movieDetailsInteractor: MovieDetailsInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewState = MediatorLiveData<RateMovieViewState>()
    val viewState: LiveData<RateMovieViewState> get() = _viewState

    private val _userMessages = MutableLiveData<RateMovieUserMessages>()
    val userMessages: LiveData<RateMovieUserMessages> get() = _userMessages

    private lateinit var currentParam: RateMovieParam

    init {
        _viewState.addSource(movieDetailsInteractor.movieStateEvents) { event ->
            when (event) {
                is NoStateFound -> TODO()
                is NotConnectedToNetwork -> TODO()
                is UserNotLogged -> TODO()
                is UnknownError -> TODO()
                is FetchSuccess -> processMovieStateUpdate(event.data, currentParam.screenTitle, currentParam.movieImageUrl)
                is RateMovie -> when (event.success) {
                    true -> RateMovieUserMessages.RATE_SUCCESS
                    false -> RateMovieUserMessages.RATE_ERROR
                }.let {
                    _userMessages.value = it
                }.also {
                    navigateTo(Destination.PreviousDestination)
                }
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: RateMovieParam) {
        currentParam = param
        fetchMovieState(
                param.movieId,
                param.screenTitle,
                param.movieImageUrl
        )
    }

    fun onRateMovie(rating: Float) {
        if (_viewState.value?.rating != rating) {
            withMovieDetailsInteractor { rateMovie(currentParam.movieId, scaleUpRatingValue(rating)) }
            _viewState.value = RateMovieViewState.showLoading(
                    currentParam.screenTitle,
                    currentParam.movieImageUrl
            )
        }
    }

    /**
     * When called, this method will push the loading view state and will fetch the movie state
     * of the movie being shown. When the fetching process is done, the view state will be updated
     * based on the result posted by the interactor.
     */
    private fun fetchMovieState(movieId: Double, movieTitle: String, movieImageUrl: String) {
        withMovieDetailsInteractor { fetchMovieState(movieId) }
        _viewState.value = RateMovieViewState.showLoading(
                movieTitle,
                movieImageUrl
        )
    }

    /**
     * Helper function to execute an [action] in the [movieDetailsInteractor] instance
     * on a background task.
     */
    private fun withMovieDetailsInteractor(action: MovieDetailsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(movieDetailsInteractor) } }
    }

    /**
     * Process the [MovieState] provided producing a [RateMovieViewState] that contains
     * the data to be shown to the user.
     */
    private fun processMovieStateUpdate(movieState: MovieState, screenTitle: String, movieImageUrl: String) {
        _viewState.value = movieState.rated.value?.toFloat()?.let {
            RateMovieViewState.showRated(
                    screenTitle,
                    movieImageUrl,
                    scaleDownRatingValue(it)
            )
        } ?: RateMovieViewState.showNoRated(
                screenTitle,
                movieImageUrl
        )
    }

    /**
     * Scale the obtained rating: since the value is a 10th-based value and we're showing
     * 5 stars, we need to scale by two in order to properly set the UI.
     */
    private fun scaleDownRatingValue(value: Float): Float = value / SCALING_FACTOR

    private fun scaleUpRatingValue(value: Float): Float = value * SCALING_FACTOR


    private companion object {
        private const val SCALING_FACTOR = 2
    }
}
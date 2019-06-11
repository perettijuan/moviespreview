package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.MovieState
import com.jpp.mpmoviedetails.MovieDetailActionViewState.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.NotConnectedToNetwork
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.UserNotLogged
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.UnknownError
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.FetchSuccess

class MovieDetailsActionViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                      private val movieDetailsInteractor: MovieDetailsInteractor) : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<MovieDetailActionViewState>>() }
    private var movieId: Double = 0.0
    private lateinit var currentMovieState: MovieState

    init {
        _viewStates.addSource(movieDetailsInteractor.movieStateEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> pushActionState(ShowError)
                is UserNotLogged -> TODO()
                is UnknownError -> pushActionState(ShowError)
                is FetchSuccess -> pushActionState(mapMovieState(event.data))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(movieId: Double) {
        this.movieId = movieId
        _viewStates.value = of(fetchMovieState(movieId))
    }

    /**
     * Called when the user selects the main action. This will
     * start the animation to show/hide the possible actions the
     * user can take.
     */
    fun onMainActionSelected() {
        viewStates.value?.peekContent()?.let { currentViewState ->
            pushActionState(when (currentViewState) {
                is ShowLoading -> ShowLoading
                is ShowError -> ShowError
                is ShowState -> {
                    currentViewState.copy(
                            shouldAnimate = true,
                            showOpen = !currentViewState.showOpen
                    )
                }
            })
        }
    }

    fun onFavoriteStateChanged() {
        withCurrentMovieState { movieState ->
            when (val currentState = viewStates.value?.peekContent()) {
                is ShowState -> pushActionState(currentState.copy(favorite = ActionButtonState.ShowAsLoading))
            }
        }
    }

    /**
     * Subscribe to this [LiveData] in order to get notified about the different states that
     * the view should render.
     */
    val viewStates: LiveData<HandledViewState<MovieDetailActionViewState>> get() = _viewStates

    private fun pushActionState(state: MovieDetailActionViewState) {
        _viewStates.value = of(state)
    }

    private fun fetchMovieState(movieId: Double): MovieDetailActionViewState {
        withMovieDetailsInteractor { fetchMovieState(movieId) }
        return ShowLoading
    }

    private fun withCurrentMovieState(action: (MovieState) -> Unit) {
        if (::currentMovieState.isInitialized) {
            action(currentMovieState)
        }
    }

    private fun mapMovieState(movieState: MovieState): MovieDetailActionViewState {
        currentMovieState = movieState

        val favState = when (movieState.favorite) {
            true -> ActionButtonState.ShowAsFilled
            false -> ActionButtonState.ShowAsEmpty
        }

        return ShowState(
                showOpen = false,
                shouldAnimate = false,
                favorite = favState,
                isInWatchlist = movieState.watchlist,
                isRated = movieState.rated
        )
    }

    private fun withMovieDetailsInteractor(action: MovieDetailsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(movieDetailsInteractor) } }
    }
}
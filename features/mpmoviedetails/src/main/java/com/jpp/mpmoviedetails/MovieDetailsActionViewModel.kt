package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.MovieState
import com.jpp.mpmoviedetails.MovieDetailActionViewState.*
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MovieDetailsActionViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                      private val movieDetailsInteractor: MovieDetailsInteractor) : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<MovieDetailActionViewState>>() }
    private var movieId: Double = 0.0
    private lateinit var currentMovieState: MovieState

    init {
        _viewStates.addSource(movieDetailsInteractor.movieStateEvents) { event ->
            when (event) {
                is None -> pushActionState(ShowNoMovieState(false, false))
                is NotConnectedToNetwork -> pushActionState(ShowError)
                is UserNotLogged -> pushActionState(processUserNotLogged())
                is UnknownError -> pushActionState(ShowError)
                is FetchSuccess -> pushActionState(processMovieStateUpdate(event.data))
                is UpdateFavorite -> pushActionState(processUpdateFavorite(event))
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
     * Called when the user retries after an error.
     */
    fun onRetry() {
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
                is ShowError -> ShowError
                is ShowLoading -> ShowLoading
                is ShowNoMovieState -> currentViewState.copy(
                        animateActionsExpanded = true,
                        showActionsExpanded = !currentViewState.showActionsExpanded
                )
                is ShowUserNotLogged -> currentViewState.copy(
                        animateActionsExpanded = true,
                        showActionsExpanded = !currentViewState.showActionsExpanded
                )
                is ShowMovieState -> currentViewState.copy(
                        animateActionsExpanded = true,
                        showActionsExpanded = !currentViewState.showActionsExpanded
                )
            })
        }
    }

    fun onFavoriteStateChanged() {
        when (val currentState = viewStates.value?.peekContent()) {
            is ShowMovieState -> {
                pushActionState(currentState.copy(favorite = ActionButtonState.ShowAsLoading))
                withMovieDetailsInteractor { updateFavoriteMovieState(currentMovieState.id, !currentMovieState.favorite) }
            }
            is ShowNoMovieState -> {
                pushActionState(ShowUserNotLogged(showActionsExpanded = currentState.expanded, animateActionsExpanded = false))
            }
            is ShowUserNotLogged -> pushActionState(currentState)
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

    /**
     * Internally called when a new [MovieState] is ready to be processed.
     * Syncs the [currentMovieState] with [movieState] and produces a new
     * view state to be rendered.
     */
    private fun processMovieStateUpdate(movieState: MovieState): MovieDetailActionViewState {
        currentMovieState = movieState

        val favState = when (movieState.favorite) {
            true -> ActionButtonState.ShowAsFilled
            false -> ActionButtonState.ShowAsEmpty
        }

        return ShowMovieState(
                showActionsExpanded = false,
                animateActionsExpanded = false,
                favorite = favState,
                isInWatchlist = movieState.watchlist,
                isRated = movieState.rated
        )
    }

    private fun processUpdateFavorite(updateFavorite: UpdateFavorite): MovieDetailActionViewState {
        return when (updateFavorite.success) {
            true -> processMovieStateUpdate(currentMovieState.copy(favorite = !currentMovieState.favorite))
            false -> processMovieStateUpdate(currentMovieState)
        }
    }

    private fun processUserNotLogged(): MovieDetailActionViewState {
        return viewStates.value?.peekContent()?.let {
            ShowUserNotLogged(showActionsExpanded = it.expanded, animateActionsExpanded = it.animate)
        } ?: ShowUserNotLogged(false, false)
    }

    private fun withMovieDetailsInteractor(action: MovieDetailsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(movieDetailsInteractor) } }
    }
}
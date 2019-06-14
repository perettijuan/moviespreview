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

/**
 * [MPScopedViewModel] to handle the actions that the user can take on  the[MovieDetailsFragment].
 * It is a coroutine-scoped ViewModel, which indicates that some work will be executed
 * in a background context and synced to the main context when over.
 *
 * It consumes data coming from the lower layers - exposed by interactors -
 * and maps that data to view logic.
 */
class MovieDetailsActionViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                      private val movieDetailsInteractor: MovieDetailsInteractor) : MPScopedViewModel(dispatchers) {

    private val _viewStates by lazy { MediatorLiveData<HandledViewState<MovieDetailActionViewState>>() }
    private var movieId: Double = 0.0
    private lateinit var currentMovieState: MovieState

    init {
        _viewStates.addSource(movieDetailsInteractor.movieStateEvents) { event ->
            when (event) {
                is NoStateFound -> pushActionState(ShowNoMovieState(false, false))
                is NotConnectedToNetwork -> pushActionState(ShowError)
                is UserNotLogged -> pushActionState(processUserNotLogged())
                is UnknownError -> pushActionState(ShowError)
                is FetchSuccess -> pushActionState(processMovieStateUpdate(event.data))
                is UpdateFavorite -> pushActionState(processUpdateFavorite(event))
                is UpdateWatchlist -> pushActionState(processUpdateWatchlist(event))
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

    /**
     * Called when the user attempts to change the favorite state of
     * the movie being handled.
     */
    fun onFavoriteStateChanged() {
        executeMovieStateUpdate({
            updateFavoriteMovieState(movieId = currentMovieState.id, asFavorite = !currentMovieState.favorite)
        }, { currentShowingState ->
            currentShowingState.copy(favorite = ActionButtonState.ShowAsLoading)
        })
    }

    /**
     * Called when the user attempts to change the watchlist state of the
     * movie being handled.
     */
    fun onWatchlistStateChanged() {
        executeMovieStateUpdate({
            updateWatchlistMovieState(movieId = currentMovieState.id, inWatchlist = !currentMovieState.watchlist)
        }, { currentShowingState ->
            currentShowingState.copy(isInWatchlist = ActionButtonState.ShowAsLoading)
        })
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

        val watchlistState = when (movieState.watchlist) {
            true -> ActionButtonState.ShowAsFilled
            false -> ActionButtonState.ShowAsEmpty
        }

        return ShowMovieState(
                showActionsExpanded = false,
                animateActionsExpanded = false,
                favorite = favState,
                isInWatchlist = watchlistState,
                isRated = movieState.rated
        )
    }

    private fun processUpdateFavorite(updateFavorite: UpdateFavorite): MovieDetailActionViewState {
        return when (updateFavorite.success) {
            true -> processMovieStateUpdate(currentMovieState.copy(favorite = !currentMovieState.favorite))
            false -> processMovieStateUpdate(currentMovieState)
        }
    }

    private fun processUpdateWatchlist(updateWatchlist: UpdateWatchlist): MovieDetailActionViewState {
        return when (updateWatchlist.success) {
            true -> processMovieStateUpdate(currentMovieState.copy(watchlist = !currentMovieState.watchlist))
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

    /**
     * Inner helper function that triggers an update on the movie state being shown, based
     * on the current UI state.
     * It receives two arguments:
     *  - [stateUpdateFunction] is the function that will be executed on the [MovieDetailsInteractor]
     *    instance to trigger the state update.
     *  - [copyLoadingStateFunction] a copy function that will provide a next UI state [ShowMovieState]
     *    based on the current one.
     *
     * Based on the current state being shown to the user, this function will allow
     * to update the state of the movie (via interactor) and update the UI state.
     */
    private fun executeMovieStateUpdate(stateUpdateFunction: MovieDetailsInteractor.() -> Unit,
                                        copyLoadingStateFunction: (ShowMovieState) -> ShowMovieState) {
        when (val currentState = viewStates.value?.peekContent()) {
            is ShowMovieState -> {
                pushActionState(copyLoadingStateFunction(currentState))
                withMovieDetailsInteractor { stateUpdateFunction() }
            }
            is ShowNoMovieState -> {
                pushActionState(ShowUserNotLogged(showActionsExpanded = currentState.expanded, animateActionsExpanded = false))
            }
            is ShowUserNotLogged -> pushActionState(currentState)
        }
    }
}
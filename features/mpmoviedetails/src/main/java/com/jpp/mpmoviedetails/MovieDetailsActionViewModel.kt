package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.MovieState
import com.jpp.mpmoviedetails.MovieDetailActionViewState.*
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the movie details actions (the data shown by the UI that is not
 * related to the actions that the user can perform is controlled by [MovieDetailsViewModel]). The VM retrieves
 * the data from the underlying layers using the provided [MovieDetailsInteractor] and maps the business
 * data to UI data, producing a [MovieDetailViewState] that represents the configuration of the view
 * at any given moment.
 *
 * When the user performs an action (either fav, add to watchlist or rate) the VM updates the state
 * of the movie internally and in the server side and updates the view layer according to the new
 * state of the business layer.
 */
class MovieDetailsActionViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                      private val movieDetailsInteractor: MovieDetailsInteractor)
    : MPScopedViewModel(dispatchers) {

    private val _viewState = MediatorLiveData<MovieDetailActionViewState>()
    val viewState: LiveData<MovieDetailActionViewState> get() = _viewState

    private var currentMovieId: Double = 0.0
    private lateinit var currentMovieState: MovieState

    init {
        _viewState.addSource(movieDetailsInteractor.movieStateEvents) { event ->
            when (event) {
                is NoStateFound -> _viewState.value = ShowNoMovieState(false, false)
                is NotConnectedToNetwork -> _viewState.value = ShowError
                is UserNotLogged -> _viewState.value = processUserNotLogged()
                is UnknownError -> _viewState.value = ShowError
                is FetchSuccess -> _viewState.value = processMovieStateUpdate(event.data)
                is UpdateFavorite -> _viewState.value = processUpdateFavorite(event)
                is UpdateWatchlist -> _viewState.value = processUpdateWatchlist(event)
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(movieId: Double) {
        if (currentMovieId == movieId) {
            // movie data is not being cached locally.
            return
        }
        currentMovieId = movieId
        fetchMovieState(movieId)
    }

    /**
     * Called when the user selects the main action. This will
     * start the animation to show/hide the possible actions the
     * user can take.
     */
    fun onMainActionSelected() {
        viewState.value?.let { currentViewState ->
            _viewState.value = when (currentViewState) {
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
            }
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
     * When called, this method will push the loading view state and will fetch the movie state
     * of the movie being shown. When the fetching process is done, the view state will be updated
     * based on the result posted by the interactor.
     */
    private fun fetchMovieState(movieId: Double) {
        withMovieDetailsInteractor { fetchMovieState(movieId) }
        _viewState.value = ShowLoading
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
        return viewState.value?.let {
            ShowUserNotLogged(showActionsExpanded = it.expanded, animateActionsExpanded = it.animate)
        } ?: ShowUserNotLogged(false, false)
    }

    /**
     * Helper function to execute an [action] in the [movieDetailsInteractor] instance
     * on a background task.
     */
    private fun withMovieDetailsInteractor(action: MovieDetailsInteractor.() -> Unit) {
        launch { withContext(Dispatchers.Default) { action(movieDetailsInteractor) } }
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
        when (val currentState = viewState.value) {
            is ShowMovieState -> {
                _viewState.value = copyLoadingStateFunction(currentState)
                withMovieDetailsInteractor { stateUpdateFunction() }
            }
            is ShowNoMovieState -> {
                _viewState.value = ShowUserNotLogged(showActionsExpanded = currentState.expanded, animateActionsExpanded = false)
            }
            is ShowUserNotLogged -> _viewState.value = currentState
        }
    }
}
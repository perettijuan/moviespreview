package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mpdomain.usecase.UpdateFavoriteMovieStateUseCase
import com.jpp.mpdomain.usecase.UpdateWatchlistMovieStateUseCase
import com.jpp.mpmoviedetails.MovieDetailActionViewState.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [ViewModel] that supports the movie details actions (the data shown by the UI that is not
 * related to the actions that the user can perform is controlled by [MovieDetailsViewModel]). The VM retrieves
 * the data from the underlying layers using the provided [MovieDetailsInteractor] and maps the business
 * data to UI data, producing a [MovieDetailViewState] that represents the configuration of the view
 * at any given moment.
 *
 * When the user performs an action (either fav, add to watchlist or rate) the VM updates the state
 * of the movie internally and in the server side and updates the view layer according to the new
 * state of the business layer.
 */
class MovieDetailsActionViewModel @Inject constructor(
    private val getMovieStateUseCase: GetMovieStateUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteMovieStateUseCase,
    private val updateWatchListUseCase: UpdateWatchlistMovieStateUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState = MutableLiveData<MovieDetailActionViewState>()
    internal val viewState: LiveData<MovieDetailActionViewState>  = _viewState

    private var currentMovieId: Double = 0.0
    private lateinit var currentMovieState: MovieState


    /**
     * Called when the view is initialized.
     */
    fun onInit(movieId: Double) {
        currentMovieId = movieId
        fetchMovieState(movieId)
    }

    fun onRetry() {
        fetchMovieState(currentMovieId)
    }

    /**
     * Called when the user selects the main action. This will
     * start the animation to show/hide the possible actions the
     * user can take.
     */
    fun onMainActionSelected() {
        viewState.value?.let { currentViewState ->
            _viewState.value = when (currentViewState) {
                is ShowReloadState -> ShowReloadState(currentViewState.expanded)
                is ShowLoading -> ShowLoading
                is ShowUserNotLogged -> currentViewState.copy(
                    animateActionsExpanded = true,
                    showActionsExpanded = !currentViewState.showActionsExpanded
                )
                is ShowNoMovieState -> currentViewState.copy(
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
            viewModelScope.launch {
                val result = withContext(ioDispatcher) {
                    updateFavoriteUseCase.execute(currentMovieState.id, !currentMovieState.favorite)
                }

                _viewState.value = when (result) {
                    is Try.Failure -> {
                        when (result.cause) {
                            is Try.FailureCause.UserNotLogged -> processUserNotLogged()
                            else -> updateErrorState()
                        }
                    }
                    is Try.Success -> processUpdateFavorite(result.value)
                }

            }
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
            viewModelScope.launch {
                val result = withContext(ioDispatcher) {
                    updateWatchListUseCase.execute(
                        currentMovieState.id,
                        !currentMovieState.watchlist
                    )
                }

                _viewState.value = when (result) {
                    is Try.Failure -> {
                        when (result.cause) {
                            is Try.FailureCause.UserNotLogged -> processUserNotLogged()
                            else -> updateErrorState()
                        }
                    }
                    is Try.Success -> processUpdateWatchlist(result.value)
                }
            }
        }, { currentShowingState ->
            currentShowingState.copy(isInWatchlist = ActionButtonState.ShowAsLoading)
        })
    }


    private fun fetchMovieState(movieId: Double) {
        _viewState.value = ShowLoading
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getMovieStateUseCase.execute(movieId)
            }

            _viewState.value = when (result) {
                is Try.Failure -> updateErrorState()
                is Try.Success -> processMovieStateUpdate(result.value)
            }
        }
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
            isRated = movieState.rated.isRated
        )
    }

    private fun processUpdateFavorite(success: Boolean): MovieDetailActionViewState {
        return when (success) {
            true -> processMovieStateUpdate(currentMovieState.copy(favorite = !currentMovieState.favorite))
            false -> processMovieStateUpdate(currentMovieState)
        }
    }

    private fun processUpdateWatchlist(success: Boolean): MovieDetailActionViewState {
        return when (success) {
            true -> processMovieStateUpdate(currentMovieState.copy(watchlist = !currentMovieState.watchlist))
            false -> processMovieStateUpdate(currentMovieState)
        }
    }

    private fun processUserNotLogged(): MovieDetailActionViewState {
        return viewState.value?.let {
            ShowUserNotLogged(
                showActionsExpanded = it.expanded,
                animateActionsExpanded = it.animate
            )
        } ?: ShowUserNotLogged(false, false)
    }


    private fun updateErrorState(): MovieDetailActionViewState {
        return viewState.value?.let { currentViewState ->
            ShowReloadState(currentViewState.expanded)
        } ?: ShowReloadState(false)
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
    private fun executeMovieStateUpdate(
        stateUpdateFunction: () -> Unit,
        copyLoadingStateFunction: (ShowMovieState) -> ShowMovieState
    ) {
        when (val currentState = viewState.value) {
            is ShowMovieState -> {
                _viewState.value = copyLoadingStateFunction(currentState)
                stateUpdateFunction()
            }
            is ShowNoMovieState -> {
                _viewState.value = ShowUserNotLogged(
                    showActionsExpanded = currentState.expanded,
                    animateActionsExpanded = false
                )
            }
            is ShowUserNotLogged -> _viewState.value = currentState
        }
    }
}

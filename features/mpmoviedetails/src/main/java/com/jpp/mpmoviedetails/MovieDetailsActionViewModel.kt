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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
class MovieDetailsActionViewModel(
    private val getMovieStateUseCase: GetMovieStateUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteMovieStateUseCase,
    private val updateWatchListUseCase: UpdateWatchlistMovieStateUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _viewState = MutableLiveData<MovieDetailActionViewState>()
    internal val viewState: LiveData<MovieDetailActionViewState> = _viewState

    private var currentMovieId: Double = 0.0

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
            _viewState.value = currentViewState.copy(
                animate = true,
                expanded = !currentViewState.expanded
            )
        }
    }

    /**
     * Called when the user attempts to change the favorite state of
     * the movie being handled.
     */
    fun onFavoriteStateChanged() {
        val currentFavorite = _viewState.value?.favoriteButtonState?.asFilled ?: return

        _viewState.value = _viewState.value?.showLoadingFavorite()

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                updateFavoriteUseCase.execute(currentMovieId, !currentFavorite)
            }

            when (result) {
                is Try.Failure -> processStateChangedError(result.cause)
                is Try.Success -> processUpdateFavorite(result.value)
            }
        }
    }

    /**
     * Called when the user attempts to change the watchlist state of the
     * movie being handled.
     */
    fun onWatchlistStateChanged() {
        val currentWatchlist = _viewState.value?.watchListButtonState?.asFilled ?: return

        _viewState.value = _viewState.value?.showLoadingWatchlist()

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                updateWatchListUseCase.execute(currentMovieId, !currentWatchlist)
            }

            when (result) {
                is Try.Failure -> processStateChangedError(result.cause)
                is Try.Success -> processUpdateWatchlist(result.value)
            }
        }
    }


    private fun fetchMovieState(movieId: Double) {
        _viewState.value = MovieDetailActionViewState.showLoading()
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getMovieStateUseCase.execute(movieId)
            }

            _viewState.value = when (result) {
                is Try.Failure -> _viewState.value?.showReload()
                is Try.Success -> processMovieStateUpdate(result.value)
            }
        }
    }

    /**
     * Internally called when a new [MovieState] is ready to be processed.
     * Syncs the [currentMovieState] with [movieState] and produces a new
     * view state to be rendered.
     */
    private fun processMovieStateUpdate(movieState: MovieState): MovieDetailActionViewState? {
        return viewState.value?.let { currentViewState ->
            val watchListButtonState = if (movieState.watchlist) {
                currentViewState.watchListButtonState.asFilled()
            } else {
                currentViewState.watchListButtonState.asEmpty()
            }

            val favoriteButtonState = if (movieState.favorite) {
                currentViewState.favoriteButtonState.asFilled()
            } else {
                currentViewState.favoriteButtonState.asEmpty()
            }

            currentViewState.showLoaded(
                watchListButtonState,
                favoriteButtonState
            )
        }
    }

    private fun processUpdateFavorite(success: Boolean) {
        viewState.value?.let { currentViewState ->
            _viewState.value = if (success) {
                currentViewState.copy(favoriteButtonState = currentViewState.favoriteButtonState.flipState())
            } else {
                currentViewState.copy()
            }
        }
    }

    private fun processUpdateWatchlist(success: Boolean) {
        viewState.value?.let { currentViewState ->
            _viewState.value = if (success) {
                currentViewState.copy(watchListButtonState = currentViewState.watchListButtonState.flipState())
            } else {
                currentViewState.copy()
            }
        }
    }

    private fun processUserNotLogged() {
        //TODO here we should send a single event to show the toast
    }

    private fun processErrorState() {
        //TODO here we should send a single event to show the toast
    }

    private fun processStateChangedError(errorCause: Try.FailureCause) {
        return when (errorCause) {
            is Try.FailureCause.UserNotLogged -> processUserNotLogged()
            else -> processErrorState()
        }
    }
}

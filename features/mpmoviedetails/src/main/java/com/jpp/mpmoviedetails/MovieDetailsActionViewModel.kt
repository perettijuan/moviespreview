package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
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
 * the data from the underlying layers and maps the business
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
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _viewState = MutableLiveData<MovieActionsViewState>()
    internal val viewState: LiveData<MovieActionsViewState> = _viewState

    private val _events = MutableLiveData<HandledEvent<MovieActionsEvent>>()
    internal val events: LiveData<HandledEvent<MovieActionsEvent>> = _events

    private var movieId: Double
        set(value) = savedStateHandle.set(MOVIE_ID_KEY, value)
        get() = savedStateHandle.get(MOVIE_ID_KEY)
            ?: throw IllegalStateException("Trying to access $MOVIE_ID_KEY when it is not yet set")

    private var isFavoriteMovie: Boolean
        set(value) = savedStateHandle.set(IS_FAVORITE_KEY, value)
        get() = savedStateHandle.get(IS_FAVORITE_KEY) ?: false

    private var isInWatchList: Boolean
        set(value) = savedStateHandle.set(IS_IN_WATCHLIST_KEY, value)
        get() = savedStateHandle.get(IS_IN_WATCHLIST_KEY) ?: false

    /**
     * Called when the view is initialized.
     */
    fun onInit(movieId: Double) {
        this.movieId = movieId
        _viewState.value = MovieActionsViewState.showLoading()
        fetchMovieState(movieId)
    }

    fun onRetry() {
        fetchMovieState(movieId)
    }

    /**
     * Called when the user attempts to change the favorite state of
     * the movie being handled.
     */
    fun onFavoriteStateChanged() {
        //TODO ANIMATE HERE
//        _viewState.value = _viewState.value?.showLoadingFavorite()

        val newState = !isFavoriteMovie
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                updateFavoriteUseCase.execute(movieId, newState)
            }

            when (result) {
                is Try.Success -> processUpdateFavorite(newState)
                is Try.Failure -> processStateChangedError(result.cause)
            }
        }
    }

    /**
     * Called when the user attempts to change the watchlist state of the
     * movie being handled.
     */
    fun onWatchlistStateChanged() {
        //TODO ANIMATE HERE
        //_viewState.value = _viewState.value?.showLoadingWatchlist()

        val newState = !isInWatchList
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                updateWatchListUseCase.execute(movieId, newState)
            }

            when (result) {
                is Try.Success -> processUpdateWatchlist(newState)
                is Try.Failure -> processStateChangedError(result.cause)
            }
        }
    }

    private fun fetchMovieState(movieId: Double) {
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getMovieStateUseCase.execute(movieId)
            }
            _viewState.value = processMovieStateUpdate(result.getOrNull())
        }
    }

    /**
     * Internally called when a new [MovieState] is ready to be processed.
     * Syncs the current view state with [movieState] and produces a new
     * view state to be rendered.
     */
    private fun processMovieStateUpdate(movieState: MovieState?): MovieActionsViewState? {
        return viewState.value?.let { currentViewState ->
            isFavoriteMovie = movieState?.favorite ?: false
            isInWatchList = movieState?.watchlist ?: false

            val watchListButtonState =
                currentViewState.watchListButtonState.updateWatchList(isInWatchList)
            val favoriteButtonState =
                currentViewState.favoriteButtonState.updateFavorite(isFavoriteMovie)


            currentViewState.showLoaded(
                favoriteButtonState,
                watchListButtonState
            )
        }
    }

    private fun processUpdateFavorite(newState: Boolean) {
        isFavoriteMovie = newState
        viewState.value?.let { currentViewState ->
            _viewState.value =
                currentViewState.copy(
                    favoriteButtonState = currentViewState.favoriteButtonState.updateFavorite(newState)
                )
        }
    }

    private fun processUpdateWatchlist(newState: Boolean) {
        isInWatchList = newState
        viewState.value?.let { currentViewState ->
            _viewState.value =
                currentViewState.copy(
                    watchListButtonState = currentViewState.watchListButtonState.updateWatchList(newState)
                )
        }
    }

    private fun processStateChangedError(errorCause: Try.FailureCause) {
        if (errorCause is Try.FailureCause.UserNotLogged) {
            _viewState.value = MovieActionsViewState.showLoading()
            _events.value = of(MovieActionsEvent.ShowUserNotLogged())
        } else {
            _events.value = of(MovieActionsEvent.ShowUnexpectedError())
        }
    }

    private fun ActionButtonState.updateWatchList(inWatchlist: Boolean): ActionButtonState {
        return if (inWatchlist) {
            watchList()
        } else {
            noWatchList()
        }
    }

    private fun ActionButtonState.updateFavorite(isFavorite: Boolean): ActionButtonState {
        return if (isFavorite) {
            favorite()
        } else {
            noFavorite()
        }
    }

    private companion object {
        const val MOVIE_ID_KEY = "MOVIE_ID_KEY"
        const val IS_FAVORITE_KEY = "IS_FAVORITE_KEY"
        const val IS_IN_WATCHLIST_KEY = "IS_IN_WATCHLIST_KEY"
    }
}

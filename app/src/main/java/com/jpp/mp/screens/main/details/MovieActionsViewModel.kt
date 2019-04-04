package com.jpp.mp.screens.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mpdomain.usecase.details.GetMovieAccountStateUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.jpp.mpdomain.usecase.details.GetMovieAccountStateUseCase.MovieAccountStateResult.*

/**
 * [MPScopedViewModel] to handle the state of the action views in MovieDetailsFragment.
 * It is a coroutine-scoped ViewModel, which indicates that some work will be executed in a
 * background context and synced to the main context when over.
 *
 * It exposes an output as a LiveData object that receives [MovieActionsState] updates as soon
 * as any new state is identified by the ViewModel.
 */
class MovieActionsViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                private val getMovieAccountStateUseCase: GetMovieAccountStateUseCase)
    : MPScopedViewModel(dispatchers) {


    private val actionsStateLiveData by lazy { MutableLiveData<MovieActionsState>() }

    /**
     * Called on initialization of the MovieDetailsFragment.
     * Each time this method is called the backing UseCase is executed in order to retrieve
     * the details of the movie identified by [movieId].
     * The updates will be posted to the [LiveData] object provided by [viewState()].
     */
    fun init(movieId: Double) {
        pushHiddenAndFetchMovieState(movieId)
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [MovieActionsState].
     */
    fun actionsState(): LiveData<MovieActionsState> = actionsStateLiveData

    /**
     * Pushes the loading state into the view and starts the process to fetch the details
     * of the movie.
     */
    private fun pushHiddenAndFetchMovieState(movieId: Double) {
        actionsStateLiveData.value = MovieActionsState.Hidden
        launch { actionsStateLiveData.value = fetchMovieAccountState(movieId) }
    }

    /**
     * Fetches the account state for the current movie being shown.
     * @return a [MovieActionsState] that is posted in actionsState in order to update the UI.
     */
    private suspend fun fetchMovieAccountState(movieId: Double): MovieActionsState = withContext(dispatchers.default()) {
        getMovieAccountStateUseCase
                .getMovieAccountState(movieId)
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorUnknown -> MovieActionsState.Hidden
                        is UserNotLogged -> MovieActionsState.Shown(isFavorite = false)
                        is ErrorNoConnectivity -> MovieActionsState.Hidden
                        is Success -> with(ucResult.movieState) {
                            MovieActionsState.Shown(isFavorite = favorite)
                        }
                    }
                }
    }
}
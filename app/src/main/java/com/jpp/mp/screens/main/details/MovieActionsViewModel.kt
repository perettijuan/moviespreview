package com.jpp.mp.screens.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpdomain.usecase.account.GetMovieAccountStateUseCase
import com.jpp.mpdomain.usecase.account.GetMovieAccountStateUseCase.MovieAccountStateResult.*
import com.jpp.mpdomain.usecase.account.MarkMovieAsFavoriteUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] to handle the state of the action views in MovieDetailsFragment.
 * It is a coroutine-scoped ViewModel, which indicates that some work will be executed in a
 * background context and synced to the main context when over.
 *
 * It exposes an output as a LiveData object that receives [MovieActionsState] updates as soon
 * as any new state is identified by the ViewModel.
 */
class MovieActionsViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                private val getMovieAccountStateUseCase: GetMovieAccountStateUseCase,
                                                private val favoriteMovieUseCase: MarkMovieAsFavoriteUseCase)
    : MPScopedViewModel(dispatchers) {


    private val actionsStateLiveData by lazy { MutableLiveData<MovieActionsState>() }

    /**
     * Called on initialization of the MovieDetailsFragment.
     * Each time this method is called the backing UseCase is executed in order to retrieve
     * the details of the movie identified by [movieId].
     * The updates will be posted to the [LiveData] object provided by [viewState()].
     */
    fun init(movieId: Double) {
        actionsStateLiveData.value = MovieActionsState.Hidden
        launch { actionsStateLiveData.value = fetchMovieAccountState(movieId) }
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [MovieActionsState].
     */
    fun actionsState(): LiveData<MovieActionsState> = actionsStateLiveData

    /**
     * Called when the user favorites the movie being shown.
     */
    fun updateMovieFavoriteState(movieId: Double) {
        val favoriteState = getFavoriteStateToUpdate()
        actionsStateLiveData.value = MovieActionsState.Updating(favorite = true)
        launch { actionsStateLiveData.value = updateFavoriteValueForMovie(movieId, favoriteState) }
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

    /**
     * Executes the use case to update the favorite state of the movie identified by [movieId].
     * @return a [MovieActionsState] that is posted in actionsState in order to update the UI.
     */
    private suspend fun updateFavoriteValueForMovie(movieId: Double, favorite: Boolean): MovieActionsState = withContext(dispatchers.default()) {
        favoriteMovieUseCase
                .favoriteMovie(movieId, favorite)
                .let { ucResult ->
                    when (ucResult) {
                        is MarkMovieAsFavoriteUseCase.FavoriteMovieResult.ErrorNoConnectivity -> MovieActionsState.Shown(isFavorite = !favorite)
                        is MarkMovieAsFavoriteUseCase.FavoriteMovieResult.ErrorUnknown -> MovieActionsState.Shown(isFavorite = !favorite)
                        is MarkMovieAsFavoriteUseCase.FavoriteMovieResult.UserNotLogged -> MovieActionsState.UserNotLoggedIn
                        is MarkMovieAsFavoriteUseCase.FavoriteMovieResult.Success -> MovieActionsState.Shown(isFavorite = favorite)
                    }
                }
    }

    private fun getFavoriteStateToUpdate(): Boolean {
        return actionsStateLiveData.value?.let { currentState ->
            when (currentState) {
                is MovieActionsState.Shown -> !currentState.isFavorite
                else -> throw IllegalStateException("Invalid view state $currentState")
            }
        } ?: run {
            throw IllegalStateException("Invalid view state NULL")
        }
    }

}
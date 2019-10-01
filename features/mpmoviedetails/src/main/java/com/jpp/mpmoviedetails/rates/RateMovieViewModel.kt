package com.jpp.mpmoviedetails.rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mpmoviedetails.MovieDetailsInteractor
import javax.inject.Inject
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RateMovieViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                             private val movieDetailsInteractor: MovieDetailsInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewState = MediatorLiveData<RateMovieViewState>()
    val viewState: LiveData<RateMovieViewState> get() = _viewState

    private lateinit var currentParam: RateMovieParam

    init {
        _viewState.addSource(movieDetailsInteractor.movieStateEvents) { event ->
            when (event) {
                is NoStateFound -> TODO()
                is NotConnectedToNetwork -> TODO()
                is UserNotLogged -> TODO()
                is UnknownError -> TODO()
                is FetchSuccess -> TODO()
            }
        }
    }


    fun onInit(param: RateMovieParam) {
        currentParam = param
        fetchMovieState(
                param.movieId,
                param.screenTitle,
                param.movieImageUrl
        )
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
}
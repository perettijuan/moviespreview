package com.jpp.mp.screens.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase.GetMovieDetailsResult.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] to handle the state of the MovieDetailsFragment. It is a coroutine-scoped
 * ViewModel, which indicates that some work will be executed in a background context and synced
 * to the main context when over.
 *
 * It exposes an output as a LiveData object that receives [MovieDetailsViewState] updates as soon
 * as any new state is identified by the ViewModel.
 *
 * It exposes an output as a LiveData object that receives [MovieDetailsNavigationEvent] updates
 * to route the view.
 */
class MovieDetailsViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                private val getMovieDetailsUseCase: GetMovieDetailsUseCase)
    : MPScopedViewModel(dispatchers) {

    private val viewStateLiveData by lazy { MutableLiveData<MovieDetailsViewState>() }
    private val navigationEvents by lazy { SingleLiveEvent<MovieDetailsNavigationEvent>() }
    private lateinit var retryFunc: () -> Unit

    /**
     * Called on initialization of the MovieDetailsFragment.
     * Each time this method is called the backing UseCase is executed in order to retrieve
     * the details of the movie identified by [movieId].
     * The updates will be posted to the [LiveData] object provided by [viewState()].
     */
    fun init(movieId: Double) {
        initImpl(movieId)
    }

    /**
     * Called when the data being shown to the user needs to be refreshed.
     */
    fun refresh(movieId: Double) {
        initImpl(movieId)
    }

    private fun initImpl(movieId: Double) {
        retryFunc = { pushLoadingAndFetchMovieDetails(movieId) }
        pushLoadingAndFetchMovieDetails(movieId)
    }

    /**
     * Subscribe to this [LiveData] in order to get updates of the [MovieDetailsViewState].
     */
    fun viewState(): LiveData<MovieDetailsViewState> = viewStateLiveData

    /**
     * Exposes the events that are triggered when a navigation event is detected.
     * We need a different LiveData here in order to avoid the problem of back navigation:
     * - The default LiveData object posts the last value every time a new observer starts observing.
     */
    fun navEvents(): LiveData<MovieDetailsNavigationEvent> = navigationEvents

    /**
     * Called in order to execute the last attempt to fetch a movie detail.
     */
    fun retry() {
        when (viewStateLiveData.value) {
            is MovieDetailsViewState.ErrorUnknown -> retryFunc.invoke()
            is MovieDetailsViewState.ErrorNoConnectivity -> retryFunc.invoke()
        }
    }

    /**
     * Called when the user selects the credits item in order to navigate to the movies credits.
     */
    fun onCreditsSelected(movieId: Double, movieTitle: String) {
        navigationEvents.value = MovieDetailsNavigationEvent.ToCredits(movieId, movieTitle)
    }

    /**
     * Called when the user has attempted to execute an action when is not logged
     * in the application.
     */
    fun userAttemptedActionWhenNotLoggedIn() {
        navigationEvents.value = MovieDetailsNavigationEvent.ToLogin
    }

    /**
     * Pushes the loading state into the view and starts the process to fetch the details
     * of the movie.
     */
    private fun pushLoadingAndFetchMovieDetails(movieId: Double) {
        viewStateLiveData.value = MovieDetailsViewState.Loading
//        launch {
//            /*
//             * fetchMovieDetail() is being executed in the default dispatcher, which indicates that is
//             * running in a different thread that the UI thread.
//             * Since the default context in ViewModel is the main context (UI thread), once
//             * that fetchMovieDetail returns its value, we're back in the main context.
//             */
//            viewStateLiveData.value = fetchMovieDetail(movieId)
//        }
    }

    /**
     * Fetches the details of the movie identified by [movieId].
     * @return a [MovieDetailsViewState] that is posted in viewState in order
     * to update the UI.
     */
//    private suspend fun fetchMovieDetail(movieId: Double): MovieDetailsViewState = withContext(dispatchers.default()) {
//        getMovieDetailsUseCase
//                .getDetailsForMovie(movieId)
//                .let { ucResult ->
//                    when (ucResult) {
//                        is ErrorNoConnectivity -> MovieDetailsViewState.ErrorNoConnectivity
//                        is ErrorUnknown -> MovieDetailsViewState.ErrorUnknown
//                        is Success -> MovieDetailsViewState.ShowDetail(mapMovieDetails(ucResult.details))
//                    }
//                }
//    }



}
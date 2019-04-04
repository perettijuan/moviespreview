package com.jpp.mp.screens.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.CoroutineDispatchers
import com.jpp.mp.screens.MPScopedViewModel
import com.jpp.mp.screens.SingleLiveEvent
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.usecase.details.GetMovieAccountStateUseCase
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
                                                private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
                                                private val getMovieAccountStateUseCase: GetMovieAccountStateUseCase)
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
     * Pushes the loading state into the view and starts the process to fetch the details
     * of the movie.
     */
    private fun pushLoadingAndFetchMovieDetails(movieId: Double) {
        viewStateLiveData.value = MovieDetailsViewState.Loading
        launch {
            /*
             * fetchMovieDetail() is being executed in the default dispatcher, which indicates that is
             * running in a different thread that the UI thread.
             * Since the default context in ViewModel is the main context (UI thread), once
             * that fetchMovieDetail returns its value, we're back in the main context.
             */
            viewStateLiveData.value = fetchMovieDetail(movieId)
        }
    }

    /**
     * Fetches the details of the movie identified by [movieId].
     * @return a [MovieDetailsViewState] that is posted in viewState in order
     * to update the UI.
     */
    private suspend fun fetchMovieDetail(movieId: Double): MovieDetailsViewState = withContext(dispatchers.default()) {
        getMovieDetailsUseCase
                .getDetailsForMovie(movieId)
                .let { ucResult ->
                    when (ucResult) {
                        is ErrorNoConnectivity -> MovieDetailsViewState.ErrorNoConnectivity
                        is ErrorUnknown -> MovieDetailsViewState.ErrorUnknown
                        is Success -> MovieDetailsViewState.ShowDetail(mapMovieDetails(ucResult.details))
                    }
                }
    }

    /**
     * Maps a domain [MovieDetail] into a UI [UiMovieDetails].
     */
    private fun mapMovieDetails(domainDetail: MovieDetail): UiMovieDetails =
            with(domainDetail) {
                UiMovieDetails(
                        title = title,
                        overview = overview,
                        releaseDate = release_date,
                        voteCount = vote_count,
                        voteAverage = vote_average,
                        popularity = popularity,
                        genres = genres.map { genre -> mapGenreToIcon(genre) }
                )
            }

    /**
     * Maps all the known genres with a given icon.
     */
    private fun mapGenreToIcon(domainGenre: MovieGenre): MovieGenreItem {
        when (domainGenre.id) {
            ACTION_GENRE_ID -> return MovieGenreItem.Action
            ADVENTURE_GENRE_ID -> return MovieGenreItem.Adventure
            ANIMATION_GENRE_ID -> return MovieGenreItem.Animation
            COMEDY_GENRE_ID -> return MovieGenreItem.Comedy
            CRIME_GENRE_ID -> return MovieGenreItem.Crime
            DOCUMENTARY_GENRE_ID -> return MovieGenreItem.Documentary
            DRAMA_GENRE_ID -> return MovieGenreItem.Drama
            FAMILY_GENRE_ID -> return MovieGenreItem.Family
            FANTASY_GENRE_ID -> return MovieGenreItem.Fantasy
            HISTORY_GENRE_ID -> return MovieGenreItem.History
            HORROR_GENRE_ID -> return MovieGenreItem.Horror
            MUSIC_GENRE_ID -> return MovieGenreItem.Music
            MYSTERY_GENRE_ID -> return MovieGenreItem.Mystery
            SCI_FY_GENRE_ID -> return MovieGenreItem.SciFi
            TV_MOVIE_GENRE_ID -> return MovieGenreItem.TvMovie
            THRILLER_GENRE_ID -> return MovieGenreItem.Thriller
            WAR_GENRE_ID -> return MovieGenreItem.War
            WESTERN_GENRE_ID -> return MovieGenreItem.Western
            else -> return MovieGenreItem.Generic
        }
    }


    private companion object GenresId {
        const val ACTION_GENRE_ID = 28
        const val ADVENTURE_GENRE_ID = 12
        const val ANIMATION_GENRE_ID = 16
        const val COMEDY_GENRE_ID = 35
        const val CRIME_GENRE_ID = 80
        const val DOCUMENTARY_GENRE_ID = 99
        const val DRAMA_GENRE_ID = 18
        const val FAMILY_GENRE_ID = 10751
        const val FANTASY_GENRE_ID = 14
        const val HISTORY_GENRE_ID = 36
        const val HORROR_GENRE_ID = 27
        const val MUSIC_GENRE_ID = 10402
        const val MYSTERY_GENRE_ID = 9648
        const val SCI_FY_GENRE_ID = 878
        const val TV_MOVIE_GENRE_ID = 10770
        const val THRILLER_GENRE_ID = 53
        const val WAR_GENRE_ID = 10752
        const val WESTERN_GENRE_ID = 37
    }

}
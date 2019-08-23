package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
import com.jpp.mp.common.viewstate.HandledViewState
import com.jpp.mp.common.viewstate.HandledViewState.Companion.of
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.MovieGenre.GenresId.ACTION_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.ADVENTURE_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.ANIMATION_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.COMEDY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.CRIME_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.DOCUMENTARY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.DRAMA_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.FAMILY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.FANTASY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.HISTORY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.HORROR_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.MUSIC_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.MYSTERY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.SCI_FY_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.THRILLER_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.TV_MOVIE_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.WAR_GENRE_ID
import com.jpp.mpdomain.MovieGenre.GenresId.WESTERN_GENRE_ID
import com.jpp.mpmoviedetails.MovieDetailViewState.*
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieDetailEvent.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [MPScopedViewModel] that supports the movie details section (only the static data, not the actions
 * that the user can perform - for the actions, check [MovieDetailsActionViewModel]). The VM retrieves
 * the data from the underlying layers using the provided [MovieDetailsInteractor] and maps the business
 * data to UI data, producing a [MovieDetailViewState] that represents the configuration of the view
 * at any given moment.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class MovieDetailsViewModel @Inject constructor(dispatchers: CoroutineDispatchers,
                                                private val movieDetailsInteractor: MovieDetailsInteractor)
    : MPScopedViewModel(dispatchers) {


    private val _viewStates by lazy { MediatorLiveData<HandledViewState<MovieDetailViewState>>() }
    val viewStates: LiveData<HandledViewState<MovieDetailViewState>> get() = _viewStates

    private val _navEvents by lazy { SingleLiveEvent<NavigateToCreditsEvent>() }
    val navEvents: LiveData<NavigateToCreditsEvent> get() = _navEvents

    private var movieId: Double = 0.0
    private lateinit var movieTitle: String

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewStates.addSource(movieDetailsInteractor.movieDetailEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewStates.value = of(ShowNotConnected)
                is UnknownError -> _viewStates.value = of(ShowError)
                is Success -> mapMovieDetails(event.data)
                is AppLanguageChanged -> _viewStates.value = of(refreshDetailsData(movieId, movieTitle))
            }
        }
    }

    /**
     * Called when the view is initialized.
     */
    fun onInit(movieId: Double, movieTitle: String) {
        this.movieId = movieId
        this.movieTitle = movieTitle
        _viewStates.value = of(executeFetchMovieDetailStep(movieId, movieTitle))
    }

    /**
     * Called when the user retries after an error.
     */
    fun onRetry() {
        _viewStates.value = of(executeFetchMovieDetailStep(movieId, movieTitle))
    }

    /**
     * Called when the user attempts to open the credits section.
     */
    fun onMovieCreditsSelected() {
        _navEvents.value = NavigateToCreditsEvent(movieId, movieTitle)
    }


    private fun executeFetchMovieDetailStep(movieId: Double, movieTitle: String): MovieDetailViewState {
        withMovieDetailsInteractor { fetchMovieDetail(movieId) }
        return ShowLoading(movieTitle)
    }

    private fun refreshDetailsData(movieId: Double, movieTitle: String): MovieDetailViewState {
        withMovieDetailsInteractor {
            flushMovieDetailsData()
            fetchMovieDetail(movieId)
        }
        return ShowLoading(movieTitle)
    }

    private fun withMovieDetailsInteractor(action: MovieDetailsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(movieDetailsInteractor) } }
    }

    /**
     * Maps a domain [MovieDetail] into a UI [MovieDetailViewState.ShowDetail].
     */
    private fun mapMovieDetails(domainDetail: MovieDetail) {
        launch {
            withContext(dispatchers.default()) {
                with(domainDetail) {
                    ShowDetail(
                            title = title,
                            overview = overview,
                            releaseDate = release_date,
                            voteCount = vote_count.toString(),
                            voteAverage = vote_average.toString(),
                            popularity = popularity.toString(),
                            genres = genres.map { genre -> mapGenreToIcon(genre) }
                    )
                }
            }.let { _viewStates.value = of(it) }
        }
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

}
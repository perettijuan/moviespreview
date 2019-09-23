package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.common.androidx.lifecycle.SingleLiveEvent
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mp.common.coroutines.MPScopedViewModel
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


    private val _viewState = MediatorLiveData<MovieDetailViewState>()
    val viewState: LiveData<MovieDetailViewState> get() = _viewState

    private val _navEvents = SingleLiveEvent<NavigateToCreditsEvent>()
    val navEvents: LiveData<NavigateToCreditsEvent> get() = _navEvents

    private lateinit var currentParam: MovieDetailsParam

    private val retry: () -> Unit = { fetchMovieDetails(currentParam.movieTitle, currentParam.movieId, currentParam.movieTitle) }

    /*
     * Map the business logic coming from the interactor into view layer logic.
     */
    init {
        _viewState.addSource(movieDetailsInteractor.movieDetailEvents) { event ->
            when (event) {
                is NotConnectedToNetwork -> _viewState.value = MovieDetailViewState.showNoConnectivityError(currentParam.movieTitle, retry)
                is UnknownError -> _viewState.value = MovieDetailViewState.showUnknownError(currentParam.movieTitle, retry)
                is Success -> mapMovieDetails(event.data, currentParam.movieTitle, currentParam.movieImageUrl)
                is AppLanguageChanged -> refreshDetailsData(currentParam.movieTitle, currentParam.movieId, currentParam.movieTitle)
            }
        }
    }

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: MovieDetailsParam) {
        // No need to verify if params are different since the app is already caching the data in the DB.
        currentParam = param
        fetchMovieDetails(
                currentParam.movieTitle,
                currentParam.movieId,
                currentParam.movieImageUrl
        )
    }

    /**
     * Called when the user wants to navigate to the movie credits section.
     * A new state is posted in [navEvents] in order to handle the event.
     */
    fun onMovieCreditsSelected() {
        _navEvents.value = NavigateToCreditsEvent(
                currentParam.movieId,
                currentParam.movieTitle
        )
    }

    /**
     * When called, this method will push the loading view state and will fetch the details
     * of the movie being shown. When the fetching process is done, the view state will be updated
     * based on the result posted by the interactor.
     */
    private fun fetchMovieDetails(movieTitle: String, movieId: Double, movieImageUrl: String) {
        withMovieDetailsInteractor { fetchMovieDetail(movieId) }
        _viewState.value = MovieDetailViewState.showLoading(movieTitle, movieImageUrl)
    }

    /**
     * Starts a refresh data process by indicating to the interactor that new data needs
     * to be fetched for the movie details being shown. This is executed in a background
     * task while the view state is updated with the loading state.
     */
    private fun refreshDetailsData(movieTitle: String, movieId: Double, movieImageUrl: String) {
        withMovieDetailsInteractor {
            flushMovieDetailsData()
            fetchMovieDetail(movieId)
        }
        _viewState.value = MovieDetailViewState.showLoading(movieTitle, movieImageUrl)
    }

    /**
     * Helper function to execute an [action] in the [movieDetailsInteractor] instance
     * on a background task.
     */
    private fun withMovieDetailsInteractor(action: MovieDetailsInteractor.() -> Unit) {
        launch { withContext(dispatchers.default()) { action(movieDetailsInteractor) } }
    }

    /**
     * Maps a domain [MovieDetail] into a UI [MovieDetailViewState] and updates
     * the state of the UI to show the details of the movie.
     */
    private fun mapMovieDetails(domainDetail: MovieDetail, movieTitle: String, imageUrl: String) {
        launch {
            withContext(dispatchers.default()) {
                with(domainDetail) {
                    MovieDetailViewState.showDetails(
                            screenTitle = movieTitle,
                            movieImageUrl = imageUrl,
                            overview = overview,
                            releaseDate = release_date,
                            voteCount = vote_count.toString(),
                            popularity = popularity.toString(),
                            genres = genres.map { genre -> mapGenreToIcon(genre) }
                    )
                }
            }.let { _viewState.value = it }
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
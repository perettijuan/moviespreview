package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.jpp.mpdomain.usecase.GetMovieDetailUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the movie details section (only the static data, not the actions
 * that the user can perform - for the actions, check [MovieDetailsActionViewModel]). The VM retrieves
 * the data from the underlying layers using the provided use cases and maps the business
 * data to UI data, producing a [MovieDetailViewState] that represents the configuration of the view
 * at any given moment.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class MovieDetailsViewModel(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val navigator: MovieDetailsNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var movieId: Double
        set(value) = savedStateHandle.set(MOVIE_ID_KEY, value)
        get() = savedStateHandle.get(MOVIE_ID_KEY)
            ?: throw IllegalStateException("Trying to access $MOVIE_ID_KEY when it is not yet set")

    private var movieTitle: String
        set(value) = savedStateHandle.set(MOVIE_TITLE_KEY, value)
        get() = savedStateHandle.get(MOVIE_TITLE_KEY)
            ?: throw IllegalStateException("Trying to access $MOVIE_TITLE_KEY when it is not yet set")

    private var movieImageUrl: String
        set(value) = savedStateHandle.set(MOVIE_IMAGE_URL_KEY, value)
        get() = savedStateHandle.get(MOVIE_IMAGE_URL_KEY)
            ?: throw IllegalStateException("Trying to access $MOVIE_IMAGE_URL_KEY when it is not yet set")

    private val _viewState = MutableLiveData<MovieDetailViewState>()
    internal val viewState: LiveData<MovieDetailViewState> = _viewState

    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    internal fun onInit(param: MovieDetailsParam) {
        movieId = param.movieId
        movieTitle = param.movieTitle
        movieImageUrl = param.movieImageUrl
        fetchMovieDetails()
    }

    /**
     * Called when the user request to login from the details actions.
     */
    internal fun onUserRequestedLogin() {
        navigator.navigateToLogin()
    }

    /**
     * Called when the user wants to navigate to the movie credits section.
     */
    internal fun onMovieCreditsSelected() {
        navigator.navigateToMovieCredits(
            movieId,
            movieTitle
        )
    }

    /**
     * Called when the user selects the rate movie option.
     */
    internal fun onRateMovieSelected() {
        navigator.navigateToRateMovie(
            movieId,
            movieImageUrl,
            movieTitle
        )
    }

    private fun fetchMovieDetails() {
        _viewState.value = MovieDetailViewState.showLoading(movieTitle, movieImageUrl)
        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getMovieDetailUseCase.execute(movieId)
            }
            when (result) {
                is Try.Failure -> processFailure(result.cause)
                is Try.Success -> processMovieDetails(result.value)
            }
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError {
                fetchMovieDetails()
            }
            else -> _viewState.value?.showUnknownError {
                fetchMovieDetails()
            }
        }
    }

    private fun processMovieDetails(movieDetail: MovieDetail) {
        val imageUrl = _viewState.value?.movieImageUrl ?: return

        viewModelScope.launch {
            val mappedGenres = withContext(ioDispatcher) {
                movieDetail.genres.map { genre -> genre.mapToGenreItem() }
            }

            _viewState.value = _viewState.value?.showDetails(
                movieImageUrl = imageUrl,
                overview = movieDetail.overview,
                releaseDate = movieDetail.release_date,
                voteCount = movieDetail.vote_count.toString(),
                popularity = movieDetail.popularity.toString(),
                genres = mappedGenres
            )
        }
    }

    private fun MovieGenre.mapToGenreItem(): MovieGenreItem {
        when (id) {
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

    private companion object {
        const val MOVIE_ID_KEY = "MOVIE_ID_KEY"
        const val MOVIE_TITLE_KEY = "MOVIE_TITLE_KEY"
        const val MOVIE_IMAGE_URL_KEY = "MOVIE_IMAGE_URL_KEY"
    }
}

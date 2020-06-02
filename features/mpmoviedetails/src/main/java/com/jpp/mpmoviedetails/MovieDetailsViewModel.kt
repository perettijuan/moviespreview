package com.jpp.mpmoviedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.coroutines.MPViewModel
import com.jpp.mp.common.navigation.Destination
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
import javax.inject.Inject

/**
 * [MPViewModel] that supports the movie details section (only the static data, not the actions
 * that the user can perform - for the actions, check [MovieDetailsActionViewModel]). The VM retrieves
 * the data from the underlying layers using the provided [MovieDetailsInteractor] and maps the business
 * data to UI data, producing a [MovieDetailViewState] that represents the configuration of the view
 * at any given moment.
 *
 * This VM is language aware, meaning that when the user changes the language of the device, the
 * VM is notified about such event and executes a refresh of both: the data stored by the application
 * and the view state being shown to the user.
 */
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : MPViewModel() {

    private val _viewState = MutableLiveData<MovieDetailViewState>()
    val viewState: LiveData<MovieDetailViewState> get() = _viewState

    private lateinit var currentParam: MovieDetailsParam


    /**
     * Called on VM initialization. The View (Fragment) should call this method to
     * indicate that it is ready to start rendering. When the method is called, the VM
     * internally verifies the state of the application and updates the view state based
     * on it.
     */
    fun onInit(param: MovieDetailsParam) {
        currentParam = param
        updateCurrentDestination(Destination.ReachedDestination(currentParam.movieTitle))
        fetchMovieDetails(currentParam.movieId, currentParam.movieImageUrl)
    }

    /**
     * Called when the user request to login from the details actions.
     */
    fun onUserRequestedLogin() {
        navigateTo(Destination.MPAccount)
    }

    /**
     * Called when the user wants to navigate to the movie credits section.
     */
    fun onMovieCreditsSelected() {
        navigateTo(
            Destination.MPCredits(
                currentParam.movieId,
                currentParam.movieTitle
            )
        )
    }

    /**
     * Called when the user selects the rate movie option.
     */
    fun onRateMovieSelected() {
        navigateTo(
            Destination.InnerDestination(
                MovieDetailsFragmentDirections.rateMovie(
                    currentParam.movieId.toString(),
                    currentParam.movieImageUrl,
                    currentParam.movieTitle
                )
            )
        )
    }

    /**
     * When called, this method will push the loading view state and will fetch the details
     * of the movie being shown. When the fetching process is done, the view state will be updated
     * based on the result posted by the interactor.
     */
    private fun fetchMovieDetails(movieId: Double, movieImageUrl: String) {
        _viewState.value = MovieDetailViewState.showLoading(movieImageUrl)
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
                fetchMovieDetails(
                    currentParam.movieId,
                    currentParam.movieTitle
                )
            }
            is Try.FailureCause.Unknown -> _viewState.value?.showUnknownError {
                fetchMovieDetails(
                    currentParam.movieId,
                    currentParam.movieTitle
                )
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
}

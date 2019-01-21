package com.jpp.moviespreview.screens.main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.moviespreview.screens.CoroutineDispatchers
import com.jpp.moviespreview.screens.MPScopedViewModel
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.repository.details.MovieDetailsRepository
import com.jpp.mpdomain.repository.details.MovieDetailsRepositoryState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(
        dispatchers: CoroutineDispatchers,
        private val detailsRepository: MovieDetailsRepository) : MPScopedViewModel(dispatchers) {

    private lateinit var viewStateLiveData: MutableLiveData<MovieDetailsFragmentViewState>
    private var currentMovieId: Double = INVALID_MOVIE_ID


    fun init(movieId: Double) {
        if (currentMovieId == movieId) {
            return
        }

        viewStateLiveData = MutableLiveData()
        viewStateLiveData.postValue(MovieDetailsFragmentViewState.Loading)
        launch {
            viewStateLiveData.postValue(withContext(dispatchers.default()) { fetchMovieDetail(movieId) })
        }
    }

    fun viewState(): LiveData<MovieDetailsFragmentViewState> = viewStateLiveData


    override fun onCleared() {
        currentMovieId = INVALID_MOVIE_ID
        super.onCleared()
    }

    private fun fetchMovieDetail(movieId: Double): MovieDetailsFragmentViewState =
        detailsRepository
                .getDetail(movieId)
                .let {
                    when (it) {
                        MovieDetailsRepositoryState.ErrorUnknown -> MovieDetailsFragmentViewState.ErrorUnknown
                        MovieDetailsRepositoryState.ErrorNoConnectivity -> MovieDetailsFragmentViewState.ErrorNoConnectivity
                        is MovieDetailsRepositoryState.Success -> MovieDetailsFragmentViewState.ShowDetail(mapMovieDetailsItem(it.detail))
                    }
                }
                .also {
                    currentMovieId = movieId
                }


    private fun mapMovieDetailsItem(domainDetail: MovieDetail): MovieDetailsItem =
        with(domainDetail) {
            MovieDetailsItem(
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
        const val INVALID_MOVIE_ID = (-1).toDouble()
    }

}
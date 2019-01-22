package com.jpp.moviespreview.screens.main.details

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jpp.moviespreview.R

/**
 * Represents the view state of the details fragment.
 */
sealed class MovieDetailsViewState {
    object Loading : MovieDetailsViewState()
    object ErrorUnknown : MovieDetailsViewState()
    object ErrorNoConnectivity : MovieDetailsViewState()
    data class ShowDetail(val detail: UiMovieDetails) : MovieDetailsViewState()
}

/**
 * Represents the details of a movie in the UI layer.
 */
data class UiMovieDetails(
        val title: String,
        val overview: String,
        val releaseDate: String,
        val voteCount: Double,
        val voteAverage: Float,
        val popularity: Float,
        val genres: List<MovieGenreItem>
)

/**
 * All the supported genres.
 */
sealed class MovieGenreItem(@DrawableRes val icon: Int, @StringRes val name: Int) {
    object Action : MovieGenreItem(R.drawable.ic_action, R.string.action_genre)
    object Adventure : MovieGenreItem(R.drawable.ic_adventure, R.string.adventure_genre)
    object Animation : MovieGenreItem(R.drawable.ic_animation, R.string.animation_genre)
    object Comedy : MovieGenreItem(R.drawable.ic_comedy, R.string.comedy_genre)
    object Crime : MovieGenreItem(R.drawable.ic_crime, R.string.crime_genre)
    object Documentary : MovieGenreItem(R.drawable.ic_documentary, R.string.documentary_genre)
    object Drama : MovieGenreItem(R.drawable.ic_drama, R.string.drama_genre)
    object Family : MovieGenreItem(R.drawable.ic_family, R.string.familiy_genre)
    object Fantasy : MovieGenreItem(R.drawable.ic_fantasy, R.string.fantasy_genre)
    object History : MovieGenreItem(R.drawable.ic_history, R.string.history_genre)
    object Horror : MovieGenreItem(R.drawable.ic_horror, R.string.horror_genre)
    object Music : MovieGenreItem(R.drawable.ic_music, R.string.music_genre)
    object Mystery : MovieGenreItem(R.drawable.ic_mystery, R.string.mystery_genre)
    object SciFi : MovieGenreItem(R.drawable.ic_science_ficcion, R.string.sci_fi_genre)
    object TvMovie : MovieGenreItem(R.drawable.ic_tv_movie, R.string.tv_movie_genre)
    object Thriller : MovieGenreItem(R.drawable.ic_thriller, R.string.thriller_genre)
    object War : MovieGenreItem(R.drawable.ic_war, R.string.war_genre)
    object Western : MovieGenreItem(R.drawable.ic_western, R.string.western_genre)
    object Generic : MovieGenreItem(R.drawable.ic_generic, R.string.generic_genre)
}
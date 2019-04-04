package com.jpp.mp.screens.main.details

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jpp.mp.R

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
 * Represents the state that the movie actions section can assume at any given time.
 */
sealed class MovieActionsState {
    object Hidden : MovieActionsState()
    object UserNotLoggedIn : MovieActionsState()
    data class Shown(val isFavorite: Boolean) : MovieActionsState()
    data class Updating(val favorite: Boolean) : MovieActionsState()
}

/**
 * Represents the navigation event that can be routed through the details section.
 */
sealed class MovieDetailsNavigationEvent {
    data class ToCredits(val movieId: Double, val movieTitle: String) : MovieDetailsNavigationEvent()
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
        val genres: List<MovieGenreItem>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiMovieDetails

        if (title != other.title) return false
        if (overview != other.overview) return false
        if (releaseDate != other.releaseDate) return false
        if (voteCount != other.voteCount) return false
        if (voteAverage != other.voteAverage) return false
        if (popularity != other.popularity) return false
        if (genres != other.genres) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + overview.hashCode()
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + voteCount.hashCode()
        result = 31 * result + voteAverage.hashCode()
        result = 31 * result + popularity.hashCode()
        result = 31 * result + genres.hashCode()
        return result
    }
}

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
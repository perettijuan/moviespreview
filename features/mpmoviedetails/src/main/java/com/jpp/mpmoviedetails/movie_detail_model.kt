package com.jpp.mpmoviedetails

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/*
 * Contains the definitions for the entire model used in the movie detail feature.
 */

/**
 * Represents the view states that the movie detail view can assume.
 */
sealed class MovieDetailViewState {
    /*
     * Shows the not connected to network state
     */
    object ShowNotConnected : MovieDetailViewState()

    /*
     * Shows the generic error screen.
     */
    object ShowError : MovieDetailViewState()

    /*
     * Shows when the VM indicates that a work is in progress.
     */
    data class ShowLoading(val title: String) : MovieDetailViewState()

    /*
     * Shows the data of the movie detail.
     */
    data class ShowDetail(val title: String,
                          val overview: String,
                          val releaseDate: String,
                          val voteCount: String,
                          val voteAverage: String,
                          val popularity: String,
                          val genres: List<MovieGenreItem>) : MovieDetailViewState()
}

/**
 * Represents the view state that the action item in the movie detail can assume.
 */
sealed class MovieDetailActionViewState(val animate: Boolean, val open: Boolean) {
    object ShowLoading : MovieDetailActionViewState(animate = false, open = false)
    object ShowError : MovieDetailActionViewState(animate = false, open = false)
    data class ShowState(val showOpen: Boolean,
                         val shouldAnimate: Boolean,
                         val favorite: ActionButtonState,
                         val isRated: Boolean,
                         val isInWatchlist: Boolean) : MovieDetailActionViewState(animate = shouldAnimate, open = showOpen)
}

sealed class ActionButtonState(@DrawableRes val resId: Int) {
    object IsFavorite : ActionButtonState(R.drawable.ic_favorite_filled)
    object IsNotFavorite : ActionButtonState(R.drawable.ic_favorite_empty)
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
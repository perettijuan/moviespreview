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
 * [animate] indicates if the actions section should perform the animation.
 * [expanded] indicates if the actions section should be shown expanded or not.
 */
sealed class MovieDetailActionViewState(val animate: Boolean, val expanded: Boolean) {
    /*
     * Shows the loading state in the actions section.
     */
    object ShowLoading : MovieDetailActionViewState(animate = false, expanded = false)
    /*
     * Shows the error state in the actions sections.
     */
    object ShowError : MovieDetailActionViewState(animate = false, expanded = false)
    /*
     * Shows the view state when there's no movie state to render. i.e.: the user is
     * not logged.
     */
    data class ShowNoMovieState(val showActionsExpanded: Boolean,
                                val animateActionsExpanded: Boolean)
        : MovieDetailActionViewState(animate = animateActionsExpanded, expanded = showActionsExpanded)
    /*
     * Shows the user not logged view state.
     */
    data class ShowUserNotLogged(val showActionsExpanded: Boolean,
                                 val animateActionsExpanded: Boolean)
        : MovieDetailActionViewState(animate = animateActionsExpanded, expanded = showActionsExpanded)
    /*
     * Renders the movie state with the provided data.
     */
    data class ShowMovieState(val showActionsExpanded: Boolean,
                              val animateActionsExpanded: Boolean,
                              val favorite: ActionButtonState,
                              val isRated: Boolean,
                              val isInWatchlist: ActionButtonState)
        : MovieDetailActionViewState(animate = animateActionsExpanded, expanded = showActionsExpanded)
}

/**
 * Represents the state of the action buttons show to the user.
 */
sealed class ActionButtonState {
    object ShowAsFilled : ActionButtonState()
    object ShowAsEmpty : ActionButtonState()
    object ShowAsLoading : ActionButtonState()
}

sealed class MovieDetailsNavigationEvent {
    /*
     * Redirects the user to the credits of the movie being shown.
     */
    data class GoToCredits(val movieId: Double, val movieTitle: String) : MovieDetailsNavigationEvent()
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
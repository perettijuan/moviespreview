package com.jpp.mpmoviedetails

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieId
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieImageUrl
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieTitle

/*
 * This file contains the definitions for the entire model used in the movie detail feature.
 */

/***************************************************************************************************
 ****************************** MOVIE DETAILS MODEL ************************************************
 ***************************************************************************************************/

/**
 * Represents the view states that the movie detail view can assume.
 */
data class MovieDetailViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val movieImageUrl: String = "emptyUrl",
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val contentViewState: MovieDetailContentViewState = MovieDetailContentViewState()) {

    companion object {
        fun showLoading(movieImageUrl: String) = MovieDetailViewState(loadingVisibility = View.VISIBLE, movieImageUrl = movieImageUrl)
        fun showUnknownError(errorHandler: () -> Unit) = MovieDetailViewState(errorViewState = ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = MovieDetailViewState(errorViewState = ErrorViewState.asConnectivity(errorHandler))
        fun showDetails(movieImageUrl: String,
                        overview: String,
                        genres: List<MovieGenreItem>,
                        popularity: String,
                        voteCount: String,
                        releaseDate: String) = MovieDetailViewState(movieImageUrl = movieImageUrl, contentViewState = MovieDetailContentViewState.buildVisible(overview, genres, popularity, voteCount, releaseDate))
    }
}


/**
 * Represents the view state of the details content.
 */
data class MovieDetailContentViewState(
        val visibility: Int = View.INVISIBLE,
        @StringRes val overviewTitle: Int = R.string.overview_title,
        val overview: String = "",
        @StringRes val genresTitle: Int = R.string.genres_title,
        val genres: List<MovieGenreItem> = emptyList(),
        @StringRes val popularityTitle: Int = R.string.popularity_title,
        val popularity: String = "",
        @StringRes val voteCountTitle: Int = R.string.vote_count_title,
        val voteCount: String = "",
        @StringRes val releaseDateTitle: Int = R.string.release_date_title,
        val releaseDate: String = "",
        @StringRes val creditsTitle: Int = R.string.movie_credits_title
) {
    companion object {
        fun buildVisible(overview: String,
                         genres: List<MovieGenreItem>,
                         popularity: String,
                         voteCount: String,
                         releaseDate: String) = MovieDetailContentViewState(
                visibility = View.VISIBLE,
                overview = overview,
                genres = genres,
                popularity = popularity,
                voteCount = voteCount,
                releaseDate = releaseDate
        )
    }
}


/**
 * Event triggered when the user attempts to go to the credits section.
 */
data class NavigateToCreditsEvent(val movieId: Double, val movieTitle: String)

/**
 * Represents an item in the list of genres that a movie can belong to.
 */
enum class MovieGenreItem(@DrawableRes val icon: Int, @StringRes val nameRes: Int) {
    Action(R.drawable.ic_action, R.string.action_genre),
    Adventure(R.drawable.ic_adventure, R.string.adventure_genre),
    Animation(R.drawable.ic_animation, R.string.animation_genre),
    Comedy(R.drawable.ic_comedy, R.string.comedy_genre),
    Crime(R.drawable.ic_crime, R.string.crime_genre),
    Documentary(R.drawable.ic_documentary, R.string.documentary_genre),
    Drama(R.drawable.ic_drama, R.string.drama_genre),
    Family(R.drawable.ic_family, R.string.familiy_genre),
    Fantasy(R.drawable.ic_fantasy, R.string.fantasy_genre),
    History(R.drawable.ic_history, R.string.history_genre),
    Horror(R.drawable.ic_horror, R.string.horror_genre),
    Music(R.drawable.ic_music, R.string.music_genre),
    Mystery(R.drawable.ic_mystery, R.string.mystery_genre),
    SciFi(R.drawable.ic_science_ficcion, R.string.sci_fi_genre),
    TvMovie(R.drawable.ic_tv_movie, R.string.tv_movie_genre),
    Thriller(R.drawable.ic_thriller, R.string.thriller_genre),
    War(R.drawable.ic_war, R.string.war_genre),
    Western(R.drawable.ic_western, R.string.western_genre),
    Generic(R.drawable.ic_generic, R.string.generic_genre)
}

/**
 * The initialization parameter for the [MovieDetailsViewModel.onInit] method.
 */
data class MovieDetailsParam(val movieId: Double,
                             val movieTitle: String,
                             val movieImageUrl: String) {
    companion object {
        fun fromArguments(arguments: Bundle?) = MovieDetailsParam(
                movieId(arguments).toDouble(),
                movieTitle(arguments),
                movieImageUrl(arguments)
        )
    }
}

/***************************************************************************************************
 ****************************** MOVIE ACTIONS MODEL ************************************************
 ***************************************************************************************************/


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
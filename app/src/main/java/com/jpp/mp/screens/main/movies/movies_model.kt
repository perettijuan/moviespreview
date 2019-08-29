package com.jpp.mp.screens.main.movies

import android.view.View
import androidx.annotation.StringRes
import androidx.paging.PagedList
import com.jpp.mp.R
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.Movie as DomainMovie

/*
 * This file contains the definitions for the entire model used in the movies list feature.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view state of the movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
data class MovieListViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val contentViewState: MovieListContentViewState = MovieListContentViewState()
) {
    companion object {
        fun showLoading() = MovieListViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) = MovieListViewState(errorViewState = ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = MovieListViewState(errorViewState = ErrorViewState.asConnectivity(errorHandler))
        fun showMovieList(pagedList: PagedList<MovieListItem>) = MovieListViewState(contentViewState = MovieListContentViewState(visibility = View.VISIBLE, movieList = pagedList))
    }
}

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class MovieListContentViewState(
        val visibility: Int = View.INVISIBLE,
        val movieList: PagedList<MovieListItem>? = null
)

/**
 * Represents an item in the list of Movies.
 */
data class MovieListItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String,
        val popularity: String,
        val voteCount: String
)

/**************************************************************************************************
 *************************************** NAVIGATION ***********************************************
 **************************************************************************************************/

/**
 * Represents the event that is triggered when the user selects a movie to see the detail.
 */
data class NavigateToDetailsEvent(
        val movieId: String,
        val movieImageUrl: String,
        val movieTitle: String,
        var positionInList: Int
)

/**************************************************************************************************
 *************************************** VM PARAMS ************************************************
 **************************************************************************************************/

/**
 * Represents the title of the screen. Note that this is not part of the view
 * state since the title of the screen does not belongs to the view hierarchy of the
 * Fragment - the screen title is in the Toolbar that belongs to the Activity hierarchy.
 */
enum class MovieListSectionTitle(@StringRes val titleRes: Int) {
    PLAYING(R.string.main_menu_now_playing),
    POPULAR(R.string.main_menu_popular),
    UPCOMING(R.string.main_menu_upcoming),
    TOP_RATED(R.string.main_menu_top_rated)
}

/**
 * The initialization parameter used for
 * [MovieListViewModel].
 */
data class MovieListParam(
        val section: MovieSection,
        val posterSize: Int,
        val backdropSize: Int
) {
    companion object {
        fun playing(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.Playing, posterSize, backdropSize)
        fun popular(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.Popular, posterSize, backdropSize)
        fun upcoming(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.Upcoming, posterSize, backdropSize)
        fun topRated(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.TopRated, posterSize, backdropSize)
    }
}
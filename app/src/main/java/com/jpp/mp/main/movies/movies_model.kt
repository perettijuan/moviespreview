package com.jpp.mp.main.movies

import android.view.View
import androidx.annotation.StringRes
import androidx.paging.PagedList
import com.jpp.mp.R
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState
import com.jpp.mpdomain.MovieSection

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
        @StringRes val screenTitle: Int,
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val contentViewState: MovieListContentViewState = MovieListContentViewState()
) {
    companion object {
        fun showLoading(@StringRes screenTitle: Int) = MovieListViewState(screenTitle = screenTitle, loadingVisibility = View.VISIBLE)
        fun showUnknownError(@StringRes screenTitle: Int, errorHandler: () -> Unit) = MovieListViewState(screenTitle = screenTitle, errorViewState = ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(@StringRes screenTitle: Int, errorHandler: () -> Unit) = MovieListViewState(screenTitle = screenTitle, errorViewState = ErrorViewState.asConnectivity(errorHandler))
        fun showMovieList(@StringRes screenTitle: Int, pagedList: PagedList<MovieListItem>) = MovieListViewState(screenTitle = screenTitle, contentViewState = MovieListContentViewState(visibility = View.VISIBLE, movieList = pagedList))
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
 * The initialization parameter used for
 * [MovieListViewModel].
 */
data class MovieListParam(
        val section: MovieSection,
        @StringRes val titleRes: Int,
        val posterSize: Int,
        val backdropSize: Int
) {
    companion object {
        fun playing(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.Playing, R.string.main_menu_now_playing, posterSize, backdropSize)
        fun popular(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.Popular, R.string.main_menu_popular, posterSize, backdropSize)
        fun upcoming(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.Upcoming, R.string.main_menu_upcoming, posterSize, backdropSize)
        fun topRated(posterSize: Int, backdropSize: Int) = MovieListParam(MovieSection.TopRated, R.string.main_menu_top_rated, posterSize, backdropSize)
    }
}
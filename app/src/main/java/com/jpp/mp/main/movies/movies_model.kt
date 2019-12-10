package com.jpp.mp.main.movies

import android.content.res.Resources
import android.view.View
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
 *************************************** VM PARAMS ************************************************
 **************************************************************************************************/

/**
 * The initialization parameter used for
 * [MovieListViewModel].
 */
data class MovieListParam(
    val section: MovieSection,
    val screenTitle: String,
    val posterSize: Int,
    val backdropSize: Int
) {
    companion object {
        fun playing(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.Playing,
                resources.getString(R.string.main_menu_now_playing),
                posterSize,
                backdropSize
        )

        fun popular(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.Popular,
                resources.getString(R.string.main_menu_popular),
                posterSize,
                backdropSize
        )

        fun upcoming(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.Upcoming,
                resources.getString(R.string.main_menu_upcoming),
                posterSize,
                backdropSize
        )

        fun topRated(resources: Resources, posterSize: Int, backdropSize: Int) = MovieListParam(
                MovieSection.TopRated,
                resources.getString(R.string.main_menu_top_rated),
                posterSize,
                backdropSize
        )
    }
}

package com.jpp.mp.main.movies

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

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
        fun showMovieList(movieList: List<MovieListItem>) = MovieListViewState(
                contentViewState = MovieListContentViewState(
                        visibility = View.VISIBLE,
                        movieList = movieList
                )
        )
    }
}

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class MovieListContentViewState(
        val visibility: Int = View.INVISIBLE,
        val movieList: List<MovieListItem> = emptyList()
)

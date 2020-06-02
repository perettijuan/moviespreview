package com.jpp.mp.main.movies

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
data class MovieListViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val screenTitle: String,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: MovieListContentViewState = MovieListContentViewState()
) {

    fun showUnknownError(errorHandler: () -> Unit): MovieListViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asUnknownError(errorHandler),
            contentViewState = MovieListContentViewState()
        )
    }

    fun showNoConnectivityError(errorHandler: () -> Unit): MovieListViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asConnectivity(errorHandler),
            contentViewState = MovieListContentViewState()
        )
    }

    fun showMovieList(movieList: List<MovieListItem>): MovieListViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = MovieListContentViewState(
                visibility = View.VISIBLE,
                movieList = movieList
            ),
            errorViewState = ErrorViewState.asNotVisible()
        )
    }

    companion object {
        fun showLoading(screenTitle: String) =
            MovieListViewState(screenTitle = screenTitle, loadingVisibility = View.VISIBLE)
    }
}

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class MovieListContentViewState(
    val visibility: Int = View.INVISIBLE,
    val movieList: List<MovieListItem> = emptyList()
)

package com.jpp.mp.main.movies

import android.view.View
import androidx.transition.Transition
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
data class MovieListViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val screenTitle: String,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: MovieListContentViewState = MovieListContentViewState(),
    val transition: Transition? = null
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

    fun showMovieList(movieList: List<MovieListItem>, transition: Transition?): MovieListViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = MovieListContentViewState(
                visibility = View.VISIBLE,
                movieList = movieList
            ),
            errorViewState = ErrorViewState.asNotVisible(),
            transition = transition
        )
    }

    companion object {
        fun showLoading(screenTitle: String) =
            MovieListViewState(screenTitle = screenTitle, loadingVisibility = View.VISIBLE)
    }
}

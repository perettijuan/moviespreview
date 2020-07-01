package com.jpp.mpmoviedetails

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view states that the movie detail view can assume.
 */
internal data class MovieDetailViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val screenTitle: String,
    val movieImageUrl: String = "emptyUrl",
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: MovieDetailContentViewState = MovieDetailContentViewState()
) {

    fun showUnknownError(errorHandler: () -> Unit): MovieDetailViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )
    }

    fun showNoConnectivityError(errorHandler: () -> Unit): MovieDetailViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )
    }

    fun showDetails(
        movieImageUrl: String,
        overview: String,
        genres: List<MovieGenreItem>,
        popularity: String,
        voteCount: String,
        releaseDate: String
    ): MovieDetailViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            movieImageUrl = movieImageUrl,
            contentViewState = MovieDetailContentViewState.buildVisible(
                overview,
                genres,
                popularity,
                voteCount,
                releaseDate
            )
        )
    }

    companion object {
        fun showLoading(screenTitle: String, movieImageUrl: String) = MovieDetailViewState(
            loadingVisibility = View.VISIBLE,
            screenTitle = screenTitle,
            movieImageUrl = movieImageUrl
        )
    }
}

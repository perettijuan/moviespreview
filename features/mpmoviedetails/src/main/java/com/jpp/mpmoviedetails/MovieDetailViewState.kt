package com.jpp.mpmoviedetails

import android.view.View
import androidx.annotation.StringRes
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view states that the movie detail view can assume.
 */
data class MovieDetailViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val movieImageUrl: String = "emptyUrl",
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: MovieDetailContentViewState = MovieDetailContentViewState()
) {

    companion object {
        fun showLoading(movieImageUrl: String) = MovieDetailViewState(
            loadingVisibility =
            View.VISIBLE,
            movieImageUrl = movieImageUrl
        )

        fun showUnknownError(errorHandler: () -> Unit) = MovieDetailViewState(
            errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

        fun showNoConnectivityError(errorHandler: () -> Unit) = MovieDetailViewState(
            errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

        fun showDetails(
            movieImageUrl: String,
            overview: String,
            genres: List<MovieGenreItem>,
            popularity: String,
            voteCount: String,
            releaseDate: String
        ) = MovieDetailViewState(
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
        fun buildVisible(
            overview: String,
            genres: List<MovieGenreItem>,
            popularity: String,
            voteCount: String,
            releaseDate: String
        ) = MovieDetailContentViewState(
            visibility = View.VISIBLE,
            overview = overview,
            genres = genres,
            popularity = popularity,
            voteCount = voteCount,
            releaseDate = releaseDate
        )
    }
}
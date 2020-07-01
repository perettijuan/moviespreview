package com.jpp.mpmoviedetails

import android.view.View
import androidx.annotation.StringRes

/**
 * Represents the view state of the details content.
 */
internal data class MovieDetailContentViewState(
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

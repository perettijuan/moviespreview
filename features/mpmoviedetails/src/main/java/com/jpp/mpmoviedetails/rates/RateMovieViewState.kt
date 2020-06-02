package com.jpp.mpmoviedetails.rates

import android.view.View


/**
 * Represents the view states that the movie detail view can assume.
 */
internal data class RateMovieViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val deleteVisibility: Int = View.INVISIBLE,
    val submitVisibility: Int = View.INVISIBLE,
    val ratingBarVisibility: Int = View.INVISIBLE,
    val movieTitle: String,
    val movieImageUrl: String,
    val rating: Float = 0.0F
) {
    companion object {
        fun showLoading(movieTitle: String, movieImageUrl: String) = RateMovieViewState(
            loadingVisibility = View.VISIBLE,
            movieTitle = movieTitle,
            movieImageUrl = movieImageUrl
        )

        fun showNoRated(movieTitle: String, movieImageUrl: String) = RateMovieViewState(
            loadingVisibility = View.INVISIBLE,
            ratingBarVisibility = View.VISIBLE,
            submitVisibility = View.VISIBLE,
            movieTitle = movieTitle,
            movieImageUrl = movieImageUrl
        )

        fun showRated(movieTitle: String, movieImageUrl: String, rating: Float) =
            RateMovieViewState(
                loadingVisibility = View.INVISIBLE,
                ratingBarVisibility = View.VISIBLE,
                deleteVisibility = View.VISIBLE,
                submitVisibility = View.VISIBLE,
                movieTitle = movieTitle,
                movieImageUrl = movieImageUrl,
                rating = rating
            )
    }
}
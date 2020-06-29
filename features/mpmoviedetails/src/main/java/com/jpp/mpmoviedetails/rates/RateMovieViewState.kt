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

    fun showRated(rating: Float): RateMovieViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            ratingBarVisibility = View.VISIBLE,
            deleteVisibility = View.VISIBLE,
            submitVisibility = View.VISIBLE,
            rating = rating
        )

    fun showNoRated(): RateMovieViewState = copy(
        loadingVisibility = View.INVISIBLE,
        ratingBarVisibility = View.VISIBLE,
        submitVisibility = View.VISIBLE
    )

    fun updateLoading(): RateMovieViewState = copy(
        loadingVisibility = View.VISIBLE,
        deleteVisibility = View.INVISIBLE,
        submitVisibility = View.INVISIBLE,
        ratingBarVisibility = View.INVISIBLE
    )

    companion object {
        fun createLoading(movieTitle: String, movieImageUrl: String) = RateMovieViewState(
            loadingVisibility = View.VISIBLE,
            movieTitle = movieTitle,
            movieImageUrl = movieImageUrl
        )
    }
}

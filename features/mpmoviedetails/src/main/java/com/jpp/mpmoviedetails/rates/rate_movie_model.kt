package com.jpp.mpmoviedetails.rates

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.jpp.mp.common.extensions.getStringOrFail
import com.jpp.mpmoviedetails.R

/*
 * This file contains the definitions for the entire model used in the movie detail feature.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view states that the movie detail view can assume.
 */
data class RateMovieViewState(
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

        fun showRated(movieTitle: String, movieImageUrl: String, rating: Float) = RateMovieViewState(
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

/**
 * Represents the messages that can be shown to the user when the rate action is completed.
 */
enum class RateMovieUserMessages(@StringRes val messageRes: Int) {
    RATE_SUCCESS(R.string.rate_movie_success_message),
    RATE_ERROR(R.string.rate_movie_error_message),
    DELETE_SUCCESS(R.string.rate_movie_deletion_success_message),
    DELETE_ERROR(R.string.rate_movie_deletion_error_message)
}

/**************************************************************************************************
 *************************************** VM PARAMS ************************************************
 **************************************************************************************************/

/**
 * The initialization parameter used for
 * [RateMovieViewModel].
 */
data class RateMovieParam(
        val movieId: Double,
        val screenTitle: String,
        val movieImageUrl: String
) {
    companion object {
        fun fromArguments(
                arguments: Bundle?
        ): RateMovieParam {
            return RateMovieParam(
                    arguments.getStringOrFail("movieId").toDouble(),
                    arguments.getStringOrFail("movieTitle"),
                    arguments.getStringOrFail("movieImageUrl")
            )
        }
    }
}
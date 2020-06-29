package com.jpp.mpmoviedetails.rates

import android.os.Bundle
import com.jpp.mp.common.extensions.getStringOrFail

/**
 * The initialization parameter used for
 * [RateMovieViewModel].
 */
internal data class RateMovieParam(
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

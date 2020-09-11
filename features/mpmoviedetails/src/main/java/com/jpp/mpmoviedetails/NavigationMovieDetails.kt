package com.jpp.mpmoviedetails

import android.os.Bundle
import com.jpp.mp.common.extensions.getStringOrFail

/**
 * Contains utilities to perform navigation to the movie details module.
 */
object NavigationMovieDetails {

    /**
     * Create the navigation arguments needed to navigate to the movie details.
     */
    fun navArgs(movieId: String, movieImageUrl: String, movieTitle: String) = Bundle()
        .apply {
            putString("movieId", movieId)
            putString("movieImageUrl", movieImageUrl)
            putString("movieTitle", movieTitle)
        }

    fun movieId(args: Bundle?) = args.getStringOrFail("movieId")
    fun movieImageUrl(args: Bundle?) = args.getStringOrFail("movieImageUrl")
    fun movieTitle(args: Bundle?) = args.getStringOrFail("movieTitle")

    internal fun paramsFromBundle(arguments: Bundle?) = MovieDetailsParam(
        movieId(arguments).toDouble(),
        movieTitle(arguments),
        movieImageUrl(arguments)
    )
}

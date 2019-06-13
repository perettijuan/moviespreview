package com.jpp.mpmoviedetails

import android.os.Bundle
import com.jpp.mp.common.extensions.getStringOrFail

/**
 * Contains utilities to perform navigation to movie details details.
 */
object NavigationMovieDetails {


    /**
     * Create the navigation arguments needed to navigate to the movie details.
     */
    fun navArgs(movieId: String, movieImageUrl: String, movieTitle: String, transitionName: String) = Bundle()
            .apply {
                putString("movieId", movieId)
                putString("movieImageUrl", movieImageUrl)
                putString("movieTitle", movieTitle)
                putString("transitionName", transitionName)
            }

    fun movieId(args: Bundle?) = args.getStringOrFail("movieId")
    fun imageUrl(args: Bundle?) = args.getStringOrFail("movieImageUrl")
    fun title(args: Bundle?) = args.getStringOrFail("movieTitle")
    fun transition(args: Bundle?) = args.getStringOrFail("transitionName")
}
package com.jpp.mpmoviedetails

import android.os.Bundle

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

    fun movieId(args: Bundle) = args.getString("movieId") ?: throw IllegalStateException("Can't find movieId in arguments")
    fun imageUrl(args: Bundle) = args.getString("movieImageUrl") ?: throw  IllegalStateException("Can't find movieImageUrl in arguments")
    fun title(args: Bundle) = args.getString("movieTitle") ?: throw  IllegalStateException("Can't find movieTitle in arguments")
    fun transition(args: Bundle) = args.getString("transitionName") ?: throw  IllegalStateException("Can't find transitionName in arguments")
}
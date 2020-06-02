package com.jpp.mpmoviedetails

import android.os.Bundle

/**
 * The initialization parameter for the [MovieDetailsViewModel.onInit] method.
 */
internal data class MovieDetailsParam(
    val movieId: Double,
    val movieTitle: String,
    val movieImageUrl: String
) {
    companion object {
        fun fromArguments(arguments: Bundle?) = MovieDetailsParam(
            NavigationMovieDetails.movieId(arguments).toDouble(),
            NavigationMovieDetails.movieTitle(arguments),
            NavigationMovieDetails.movieImageUrl(arguments)
        )
    }
}
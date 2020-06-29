package com.jpp.mpmoviedetails

/**
 * The initialization parameter for the [MovieDetailsViewModel.onInit] method.
 */
internal data class MovieDetailsParam(
    val movieId: Double,
    val movieTitle: String,
    val movieImageUrl: String
)

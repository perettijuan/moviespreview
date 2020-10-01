package com.jpp.mp.main.discover

/**
 * Provides navigation to the discover movies section.
 */
interface DiscoverMoviesNavigator {

    fun navigateToMovieDetails(
        movieId: String,
        movieImageUrl: String,
        movieTitle: String
    )
}
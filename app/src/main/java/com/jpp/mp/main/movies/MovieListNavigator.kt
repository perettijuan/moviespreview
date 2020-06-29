package com.jpp.mp.main.movies

/**
 * Provides navigation for the Movie list section
 */
interface MovieListNavigator {

    fun navigateToMovieDetails(
        movieId: String,
        movieImageUrl: String,
        movieTitle: String
    )

    fun navigateToSearch()
    fun navigateToAboutSection()
}

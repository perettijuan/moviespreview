package com.jpp.mp.main.movies

import android.view.View

/**
 * Provides navigation for the Movie list section
 */
interface MovieListNavigator {

    fun navigateToMovieDetails(
        movieId: String,
        movieImageUrl: String,
        movieTitle: String,
        transitionView: View
    )

    fun navigateToSearch()
    fun navigateToAboutSection()
}

package com.jpp.mpaccount.account.lists

/**
 * Provides navigation to the user movie list section.
 */
interface UserMovieListNavigator {

    fun navigateHome()

    fun navigateToMovieDetails(
        movieId: String,
        movieImageUrl: String,
        movieTitle: String
    )
}

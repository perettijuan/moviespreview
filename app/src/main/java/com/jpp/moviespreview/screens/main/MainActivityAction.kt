package com.jpp.moviespreview.screens.main

/**
 * Represents an action that is triggered by the user in the MainActivity and has a consequence
 * on the MainActivity UI.
 */
sealed class MainActivityAction {
    data class UserSelectedMovieDetails(val movieImageUrl: String, val movieTitle: String) : MainActivityAction()
    object UserSelectedMovieList : MainActivityAction()
}
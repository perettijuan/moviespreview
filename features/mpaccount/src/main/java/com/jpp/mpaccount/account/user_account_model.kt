package com.jpp.mpaccount.account

/*
 * Contains the definitions for the entire model used in the user account feature.
 */

/**
 * Represents the view states that the user account view can assume.
 */
sealed class UserAccountViewState {
    /*
     * Shows the not connected to network state
     */
    object ShowNotConnected : UserAccountViewState()
    /*
     * Shows when the VM indicates that a work is in progress.
     */
    object Loading : UserAccountViewState()

    /*
     * Shows the generic error screen.
     */
    object ShowError : UserAccountViewState()

    /*
     * Shows the user account data.
     */
    data class ShowUserAccountData(
            val avatarUrl: String,
            val userName: String,
            val accountName: String,
            val defaultLetter: Char,
            val favoriteMovieState: UserMoviesViewState,
            val ratedMovieState: UserMoviesViewState,
            val watchListState: UserMoviesViewState
    ) : UserAccountViewState()
}

/**
 * Represents the view state of the user movies.
 */
sealed class UserMoviesViewState {
    /*
     * Shown when the user has no movies.
     */
    object ShowNoMovies: UserMoviesViewState()

    /*
     * Shows an error state in the movie section.
     */
    object ShowError: UserMoviesViewState()

    /*
     * Shows the list of movies.
     */
    data class ShowUserMovies(val items: List<UserMovieItem>) : UserMoviesViewState()
}

/**
 * Represents an item shown in the user movies section.
 */
data class UserMovieItem(val image: String) : UserAccountMoviesView.UserAccountMovieItem {
    override fun getImageUrl(): String = image
}

/**
 * Represents all the navigation events that the user account view will response to.
 */
sealed class UserAccountNavigationEvent {
    /*
     * Used when the VM detects that the user is not logged in.
     */
    object GoToLogin : UserAccountNavigationEvent()

}

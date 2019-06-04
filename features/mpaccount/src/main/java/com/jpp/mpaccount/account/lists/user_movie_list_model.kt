package com.jpp.mpaccount.account.lists

import androidx.paging.PagedList

/*
 * Contains the definitions for the entire model used in the user account feature.
 */

sealed class UserMovieListViewState {
    /*
     * Shows the not connected to network state
     */
    object ShowNotConnected : UserMovieListViewState()

    /*
     * Shows when the VM indicates that a work is in progress.
     */
    object ShowLoading : UserMovieListViewState()

    /*
     * Shows the generic error screen.
     */
    object ShowError : UserMovieListViewState()

    /*
     * Shows the empty state.
     */
    object ShowEmptyList : UserMovieListViewState()

    /*
     * Shows the list of movies.
     */
    data class ShowMovieList(val pagedList: PagedList<UserMovieItem>) : UserMovieListViewState()
}

/**
 * Represents all the navigation events that the user movie list view will response to.
 */
sealed class UserMovieListNavigationEvent {
    /*
     * Redirects the user to the previous step
     */
    object GoToUserAccount : UserMovieListNavigationEvent()
}

data class UserMovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String
)
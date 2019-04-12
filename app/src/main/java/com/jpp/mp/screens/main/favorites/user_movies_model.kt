package com.jpp.mp.screens.main.favorites

import androidx.paging.PagedList

/**
 * Represents the view state of the movies view (UserMoviesFragment).
 * Each subclass of this sealed class represents a particular state that the fragment
 * can assume.
 */
sealed class UserMoviesViewState {
    object Loading : UserMoviesViewState()
    object ErrorNoConnectivity : UserMoviesViewState()
    object ErrorNoConnectivityWithItems : UserMoviesViewState()
    object ErrorUnknown : UserMoviesViewState()
    object ErrorUnknownWithItems: UserMoviesViewState()
    object UserNotLogged : UserMoviesViewState()
    object NoMovies : UserMoviesViewState()
    data class InitialPageLoaded(val pagedList: PagedList<UserMovieItem>) : UserMoviesViewState()
}

data class UserMovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String
)
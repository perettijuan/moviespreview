package com.jpp.mp.screens.main.favorites

import androidx.paging.PagedList

/**
 * Represents the view state of the movies view (FavoriteMoviesFragment).
 * Each subclass of this sealed class represents a particular state that the fragment
 * can assume.
 */
sealed class FavoriteMoviesViewState {
    object Loading : FavoriteMoviesViewState()
    object ErrorNoConnectivity : FavoriteMoviesViewState()
    object ErrorNoConnectivityWithItems : FavoriteMoviesViewState()
    object ErrorUnknown : FavoriteMoviesViewState()
    object ErrorUnknownWithItems: FavoriteMoviesViewState()
    object UserNotLogged : FavoriteMoviesViewState()
    object NoFavorites : FavoriteMoviesViewState()
    data class InitialPageLoaded(val pagedList: PagedList<FavoriteMovieItem>) : FavoriteMoviesViewState()
}

data class FavoriteMovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String
)
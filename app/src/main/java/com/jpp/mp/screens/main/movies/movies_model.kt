package com.jpp.mp.screens.main.movies

import com.jpp.mpdomain.Movie as DomainMovie

import androidx.paging.PagedList

/**
 * Represents the view state of the movies view (MoviesFragment).
 * Each subclass of this sealed class represents a particular state that the fragment
 * can assume.
 */
sealed class MoviesViewState {
    object Loading : MoviesViewState()
    object Refreshing : MoviesViewState()
    object ErrorNoConnectivity : MoviesViewState()
    object ErrorNoConnectivityWithItems : MoviesViewState()
    object ErrorUnknown : MoviesViewState()
    object ErrorUnknownWithItems : MoviesViewState()
    data class InitialPageLoaded(val pagedList: PagedList<MovieItem>) : MoviesViewState()
}

/**
 * Represents the navigation events that can be routed through the onSearch section.
 */
sealed class MoviesViewNavigationEvent {
    data class ToMovieDetails(val movieId: String, val movieImageUrl: String, val movieTitle: String, var positionInList: Int) : MoviesViewNavigationEvent()
}

/**
 * Represents an item in the list of Movies shown in the initial screen of the application.
 */
data class MovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String,
        val popularity: String,
        val voteCount: String
)
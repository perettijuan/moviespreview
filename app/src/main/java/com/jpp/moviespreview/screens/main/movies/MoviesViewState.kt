package com.jpp.moviespreview.screens.main.movies

import androidx.paging.PagedList

/**
 * Represents the view state of the movies view (MoviesFragment).
 * Each subclass of this sealed class represents a particular state that the fragment
 * can assume.
 */
sealed class MoviesViewState {
    object Idle : MoviesViewState()
    object Loading : MoviesViewState()
    object ErrorNoConnectivity : MoviesViewState()
    object ErrorNoConnectivityWithItems : MoviesViewState()
    object ErrorUnknown : MoviesViewState()
    object ErrorUnknownWithItems : MoviesViewState()
    data class InitialPageLoaded(val pagedList: PagedList<MovieItem>) : MoviesViewState()
}
package com.jpp.moviespreview.screens.main.movies

sealed class MoviesFragmentViewState {
    object None : MoviesFragmentViewState()
    object Loading : MoviesFragmentViewState()
    object ErrorNoConnectivity : MoviesFragmentViewState()
    object ErrorNoConnectivityWithItems : MoviesFragmentViewState()
    object ErrorUnknown : MoviesFragmentViewState()
    object ErrorUnknownWithItems : MoviesFragmentViewState()
    object InitialPageLoaded : MoviesFragmentViewState()
}
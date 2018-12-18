package com.jpp.moviespreview.screens.main.movies

sealed class MoviesFragmentViewState {
    object Loading : MoviesFragmentViewState()
    object ErrorNoConnectivity : MoviesFragmentViewState()
    object ErrorUnknown : MoviesFragmentViewState()
    object InitialPageLoaded : MoviesFragmentViewState()
}
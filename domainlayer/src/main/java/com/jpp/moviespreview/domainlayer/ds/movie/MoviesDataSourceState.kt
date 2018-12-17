package com.jpp.moviespreview.domainlayer.ds.movie

sealed class MoviesDataSourceState {
    object LoadingInitial : MoviesDataSourceState()
    object LoadingInitialDone : MoviesDataSourceState()
    object ErrorNoConnectivity : MoviesDataSourceState()
    object ErrorUnknown : MoviesDataSourceState()
    object LoadingAfter : MoviesDataSourceState()
    object LoadingAfterDone : MoviesDataSourceState()
}
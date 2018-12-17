package com.jpp.moviespreview.domainlayer.ds.movie

/**
 * Represents the state in which the MoviesPagingDataSource is at any given moment.
 */
sealed class MoviesDataSourceState {
    object LoadingInitial : MoviesDataSourceState()
    object LoadingInitialDone : MoviesDataSourceState()
    object ErrorNoConnectivity : MoviesDataSourceState()
    object ErrorUnknown : MoviesDataSourceState()
    object LoadingAfter : MoviesDataSourceState()
    object LoadingAfterDone : MoviesDataSourceState()
}
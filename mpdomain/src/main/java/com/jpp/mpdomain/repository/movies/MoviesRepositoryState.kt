package com.jpp.mpdomain.repository.movies
/**
 * Represents the state of an operation executed by the repository system.
 */
sealed class MoviesRepositoryState {
    object None : MoviesRepositoryState()
    object Loaded : MoviesRepositoryState()
    object Loading : MoviesRepositoryState()
    data class ErrorUnknown(val hasItems: Boolean) : MoviesRepositoryState()
    data class ErrorNoConnectivity(val hasItems: Boolean) : MoviesRepositoryState()
}
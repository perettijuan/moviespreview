package com.jpp.mpdomain.repository
/**
 * Represents the state of an operation executed by the repository system.
 */
sealed class RepositoryState {
    object None : RepositoryState()
    object Loaded : RepositoryState()
    object Loading : RepositoryState()
    data class ErrorUnknown(val hasItems: Boolean) : RepositoryState()
    data class ErrorNoConnectivity(val hasItems: Boolean) : RepositoryState()
}
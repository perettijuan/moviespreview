package com.jpp.mpdomain.repository.search

sealed class SearchRepositoryState {
    object Loading : SearchRepositoryState()
    object Loaded : SearchRepositoryState()
    data class ErrorNoConnectivity(val hasItems: Boolean) : SearchRepositoryState()
    data class ErrorUnknown(val hasItems: Boolean) : SearchRepositoryState()
}
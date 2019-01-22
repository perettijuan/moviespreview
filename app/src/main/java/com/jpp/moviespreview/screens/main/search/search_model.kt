package com.jpp.moviespreview.screens.main.search

/**
 * Represents the view state of the search screen.
 */
sealed class SearchViewState {
    object Idle : SearchViewState()
    object Searching : SearchViewState()
    object ErrorUnknown : SearchViewState()
    object DoneSearching : SearchViewState()
}
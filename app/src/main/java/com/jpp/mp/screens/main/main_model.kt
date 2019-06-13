package com.jpp.mp.screens.main

/**
 * Represents the view state that the MainActivity can show at any given time.
 */
data class MainActivityViewState(val sectionTitle: String, val menuBarEnabled: Boolean, val searchEnabled: Boolean)


sealed class SearchEvent {
    object ClearSearch : SearchEvent()
    data class Search(val query: String) : SearchEvent()
}
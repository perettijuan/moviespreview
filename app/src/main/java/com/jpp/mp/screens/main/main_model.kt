package com.jpp.mp.screens.main

/**
 * Represents the view state that the MainActivity can show at any given time.
 */
sealed class MainActivityViewState(val sectionTitle: String, val menuBarEnabled: Boolean, val searchEnabled: Boolean) {
    data class ActionBarLocked(val abTitle: String, val withAnimation: Boolean, val menuEnabled: Boolean, val isSearch: Boolean) : MainActivityViewState(abTitle, menuEnabled, isSearch)
    data class ActionBarUnlocked(val abTitle: String, val contentImageUrl: String) : MainActivityViewState(abTitle, menuBarEnabled = false, searchEnabled = false)
}


sealed class SearchEvent {
    object ClearSearch : SearchEvent()
    data class Search(val query: String) : SearchEvent()
}
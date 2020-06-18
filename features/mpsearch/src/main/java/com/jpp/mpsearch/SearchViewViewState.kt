package com.jpp.mpsearch

import android.view.View

/**
 * Represents the view state of the search view.
 */
internal data class SearchViewViewState(
    val visibility: Int = View.VISIBLE,
    val searchQuery: String = "",
    val focused: Boolean = false,
    val queryHint: Int = R.string.search_hint
) {
    companion object {
        fun showSearching(query: String): SearchViewViewState =
            SearchViewViewState(searchQuery = query, focused = false)

        fun showCleanState(): SearchViewViewState = SearchViewViewState(
            searchQuery = "",
            focused = true
        )
    }
}
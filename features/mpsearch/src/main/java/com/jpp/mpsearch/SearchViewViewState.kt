package com.jpp.mpsearch

/**
 * Represents the view state of the search view.
 */
internal data class SearchViewViewState(
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
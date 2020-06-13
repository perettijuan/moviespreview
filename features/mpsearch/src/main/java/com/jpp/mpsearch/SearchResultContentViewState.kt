package com.jpp.mpsearch

import android.view.View
import androidx.paging.PagedList

/**
 * Represents the view state of the content shown in the search section.
 */
internal data class SearchResultContentViewState(
    val searchResultsVisibility: Int = View.INVISIBLE,
    val searchResultList: PagedList<SearchResultItem>? = null,
    val emptySearchResultsVisibility: Int = View.INVISIBLE,
    val emptySearchTextRes: Int = R.string.empty_search
) {

    companion object {
        fun showResults(results: PagedList<SearchResultItem>) = SearchResultContentViewState(
            searchResultsVisibility = View.VISIBLE,
            searchResultList = results
        )

        fun showNoResults() =
            SearchResultContentViewState(emptySearchResultsVisibility = View.VISIBLE)
    }
}
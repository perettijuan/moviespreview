package com.jpp.mpsearch

import android.view.View
import com.jpp.mp.common.extensions.addList

/**
 * Represents the view state of the content shown in the search section.
 */
internal data class SearchResultContentViewState(
    val searchResultsVisibility: Int = View.INVISIBLE,
    val searchResultList: List<SearchResultItem> = emptyList(),
    val emptySearchResultsVisibility: Int = View.INVISIBLE,
    val emptySearchTextRes: Int = R.string.empty_search
) {
    fun showResults(results: List<SearchResultItem>): SearchResultContentViewState = copy(
        searchResultsVisibility = View.VISIBLE,
        searchResultList = searchResultList.toMutableList().addList(results)
    )

    fun showNoResults(): SearchResultContentViewState =
        copy(
            emptySearchResultsVisibility = View.VISIBLE,
            searchResultList = emptyList()
        )
}

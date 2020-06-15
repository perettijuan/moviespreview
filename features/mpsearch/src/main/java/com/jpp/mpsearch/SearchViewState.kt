package com.jpp.mpsearch

import android.view.View
import androidx.paging.PagedList
import com.jpp.mpdesign.views.MPErrorView

/**
 * Represents the view state that the search view ([SearchFragment]) can assume at any given point.
 */
internal data class SearchViewState(
    val searchQuery: String = "",
    val loadingVisibility: Int = View.INVISIBLE,
    val placeHolderViewState: SearchPlaceHolderViewState = SearchPlaceHolderViewState(),
    val errorViewState: MPErrorView.ErrorViewState = MPErrorView.ErrorViewState.asNotVisible(),
    val contentViewState: SearchResultContentViewState = SearchResultContentViewState()
) {
    companion object {
        fun showCleanState() = SearchViewState(
            searchQuery = "",
            placeHolderViewState = SearchPlaceHolderViewState(visibility = View.VISIBLE)
        )

        fun showSearching(query: String) =
            SearchViewState(searchQuery = query, loadingVisibility = View.VISIBLE)

        fun showUnknownError(query: String, errorHandler: () -> Unit) = SearchViewState(
            searchQuery = query,
            errorViewState = MPErrorView.ErrorViewState.asUnknownError(errorHandler)
        )

        fun showNoConnectivityError(query: String, errorHandler: () -> Unit) = SearchViewState(
            searchQuery = query,
            errorViewState = MPErrorView.ErrorViewState.asConnectivity(errorHandler)
        )

        fun showSearchResult(query: String, searchResultList: List<SearchResultItem>) =
            SearchViewState(
                searchQuery = query,
                contentViewState = SearchResultContentViewState.showResults(searchResultList)
            )

        fun showNoResults(query: String) = SearchViewState(
            searchQuery = query,
            contentViewState = SearchResultContentViewState.showNoResults()
        )
    }
}


package com.jpp.mpsearch

import android.view.View
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

    fun showSearchResult(searchResultList: List<SearchResultItem>): SearchViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = contentViewState.showResults(searchResultList),
            errorViewState = MPErrorView.ErrorViewState.asNotVisible()
        )

    fun showNoResults(query: String): SearchViewState = SearchViewState(
        searchQuery = query,
        contentViewState = contentViewState.showNoResults()
    )

    fun showUnknownError(query: String, errorHandler: () -> Unit) = SearchViewState(
        searchQuery = query,
        errorViewState = MPErrorView.ErrorViewState.asUnknownError(errorHandler)
    )

    fun showNoConnectivityError(query: String, errorHandler: () -> Unit) = SearchViewState(
        searchQuery = query,
        errorViewState = MPErrorView.ErrorViewState.asConnectivity(errorHandler)
    )

    fun showSearching(query: String) =
        SearchViewState(searchQuery = query, loadingVisibility = View.VISIBLE)

    companion object {
        fun showCleanState() = SearchViewState(
            searchQuery = "",
            placeHolderViewState = SearchPlaceHolderViewState(visibility = View.VISIBLE)
        )
    }
}


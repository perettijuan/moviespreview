package com.jpp.mpsearch

import android.view.View
import com.jpp.mpdesign.views.MPErrorView

/**
 * Represents the view state that the search view ([SearchFragment]) can assume at any given point.
 */
internal data class SearchContentViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val placeHolderViewState: SearchPlaceHolderViewState = SearchPlaceHolderViewState(),
    val errorViewState: MPErrorView.ErrorViewState = MPErrorView.ErrorViewState.asNotVisible(),
    val contentViewState: SearchResultContentViewState = SearchResultContentViewState()
) {

    fun showSearchResult(searchResultList: List<SearchResultItem>): SearchContentViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = contentViewState.showResults(searchResultList),
            errorViewState = MPErrorView.ErrorViewState.asNotVisible()
        )

    fun showNoResults(): SearchContentViewState = SearchContentViewState(
        contentViewState = contentViewState.showNoResults()
    )

    fun showUnknownError(errorHandler: () -> Unit) = SearchContentViewState(
        errorViewState = MPErrorView.ErrorViewState.asUnknownError(errorHandler)
    )

    fun showNoConnectivityError(errorHandler: () -> Unit) = SearchContentViewState(
        errorViewState = MPErrorView.ErrorViewState.asConnectivity(errorHandler)
    )

    fun showSearching() =
        SearchContentViewState(loadingVisibility = View.VISIBLE)

    companion object {
        fun showCleanState() = SearchContentViewState(
            placeHolderViewState = SearchPlaceHolderViewState(visibility = View.VISIBLE)
        )
    }
}

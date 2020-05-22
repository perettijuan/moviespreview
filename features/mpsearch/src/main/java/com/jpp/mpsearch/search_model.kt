package com.jpp.mpsearch

import android.view.View
import androidx.annotation.DrawableRes
import androidx.paging.PagedList
import com.jpp.mpdesign.views.MPErrorView

/*
 * This file contains the definitions for the entire model used in the search feature.
 */

/**
 * Represents the view state that the search view can assume at any given point.
 */
data class SearchViewState(
        val searchQuery: String = "",
        val searchHint: Int = R.string.search_hint,
        val loadingVisibility: Int = View.INVISIBLE,
        val placeHolderViewState: SearchPlaceHolderViewState = SearchPlaceHolderViewState(),
        val errorViewState: MPErrorView.ErrorViewState = MPErrorView.ErrorViewState.asNotVisible(),
        val contentViewState: SearchResultContentViewState = SearchResultContentViewState()
) {
    companion object {
        fun showCleanState() = SearchViewState(searchQuery = "", placeHolderViewState = SearchPlaceHolderViewState(visibility = View.VISIBLE))
        fun showSearching(query: String) = SearchViewState(searchQuery = query, loadingVisibility = View.VISIBLE)
        fun showUnknownError(query: String, errorHandler: () -> Unit) = SearchViewState(searchQuery = query, errorViewState = MPErrorView.ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(query: String, errorHandler: () -> Unit) = SearchViewState(searchQuery = query, errorViewState = MPErrorView.ErrorViewState.asConnectivity(errorHandler))
        fun showSearchResult(query: String, searchResultList: PagedList<SearchResultItem>) = SearchViewState(searchQuery = query, contentViewState = SearchResultContentViewState.showResults(searchResultList))
        fun showNoResults(query: String) = SearchViewState(searchQuery = query, contentViewState = SearchResultContentViewState.showNoResults())
    }
}

/**
 * Represents the view state of the search placeholder view.
 */
data class SearchPlaceHolderViewState(
        val visibility: Int = View.INVISIBLE,
        val icon: Int = R.drawable.ic_app_icon_black
)

/**
 * Represents the view state of the content shown in the search section.
 */
data class SearchResultContentViewState(
        val searchResultsVisibility: Int = View.INVISIBLE,
        val searchResultList: PagedList<SearchResultItem>? = null,
        val emptySearchResultsVisibility: Int = View.INVISIBLE,
        val emptySearchTextRes: Int = R.string.empty_search
) {

    companion object {
        fun showResults(results: PagedList<SearchResultItem>) = SearchResultContentViewState(searchResultsVisibility = View.VISIBLE, searchResultList = results)
        fun showNoResults() = SearchResultContentViewState(emptySearchResultsVisibility = View.VISIBLE)
    }
}

/**
 * Represents an item in the list of search results.
 */
data class SearchResultItem(
        val id: Double,
        val imagePath: String,
        val name: String,
        val icon: SearchResultTypeIcon
) {
    fun isMovieType() = icon == SearchResultTypeIcon.Movie
}

/**
 * Represents the icon in the type of the [SearchResultItem].
 */
enum class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    Movie(R.drawable.ic_clapperboard),
    Person(R.drawable.ic_person_black)
}

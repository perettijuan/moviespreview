package com.jpp.mpsearch

import androidx.annotation.DrawableRes
import androidx.paging.PagedList

/*
 * Contains the definitions for the entire model used in the user account feature.
 */

sealed class SearchViewState {
    /*
     * Shows the onSearch view to provide a onSearch option to the user.
     */
    object ShowSearchView : SearchViewState()

    /*
     * Shows the not connected to network state.
     */
    object ShowNotConnected : SearchViewState()

    /*
     * Shows the loading state when the VM is searching.
     */
    object ShowSearching : SearchViewState()

    /*
     * Shows the generic error screen.
     */
    object ShowError : SearchViewState()

    /*
     * Shows the empty onSearch status on screen.
     */
    data class ShowEmptySearch(val searchText: String) : SearchViewState()

    /*
     * Shows the list of onSearch results.
     */
    data class ShowSearchResults(val pagedList: PagedList<SearchResultItem>) : SearchViewState()
}


/**
 * Represents an item in the list of onSearch results.
 */
data class SearchResultItem(val id: Double,
                            val imagePath: String,
                            val name: String,
                            val icon: SearchResultTypeIcon)

/**
 * Represents the icon in the type of the [SearchResultItem].
 */
enum class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    Movie(R.drawable.ic_clapperboard),
    Person(R.drawable.ic_person_black)
}
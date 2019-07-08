package com.jpp.mp.screens.main.search

import androidx.annotation.DrawableRes
import androidx.paging.PagedList
import com.jpp.mp.R

/**
 * Represents the view state of the searchPage screen.
 */
sealed class SearchViewState {
    object Idle : SearchViewState()
    object ErrorUnknown : SearchViewState()
    object ErrorUnknownWithItems : SearchViewState()
    object ErrorNoConnectivity : SearchViewState()
    object ErrorNoConnectivityWithItems : SearchViewState()
    object Searching : SearchViewState()
    data class EmptySearch(val searchText: String) : SearchViewState()
    data class DoneSearching(val pagedList: PagedList<SearchResultItem>) : SearchViewState()
}

/**
 * Represents the navigation events that can be routed through the onSearch section.
 */
sealed class SearchViewNavigationEvent {
    data class ToMovieDetails(val movieId: String, val movieImageUrl: String, val movieTitle: String) : SearchViewNavigationEvent()
    data class ToPerson(val personId: String, val personImageUrl: String, val personName: String) : SearchViewNavigationEvent()
}

sealed class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    object MovieType : SearchResultTypeIcon(R.drawable.ic_clapperboard)
    object PersonType : SearchResultTypeIcon(R.drawable.ic_person_black)
}


data class SearchResultItem(val id: Double,
                            val imagePath: String,
                            val name: String,
                            val icon: SearchResultTypeIcon)
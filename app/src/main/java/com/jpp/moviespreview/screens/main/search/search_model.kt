package com.jpp.moviespreview.screens.main.search

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.jpp.moviespreview.R

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

sealed class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    object MovieType : SearchResultTypeIcon(R.drawable.ic_clapperboard)
    object PersonType : SearchResultTypeIcon(R.drawable.ic_person_black)
}


data class SearchResultItem(val id: Double,
                            val imagePath: String,
                            val name: String,
                            val icon: SearchResultTypeIcon)
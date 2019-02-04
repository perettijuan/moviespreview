package com.jpp.moviespreview.screens.main.search

import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.jpp.moviespreview.R

/**
 * Represents the view state of the search screen.
 */
sealed class SearchViewState {
    object Idle : SearchViewState()
    object Searching : SearchViewState()
    object ErrorUnknown : SearchViewState()
    object ErrorUnknownWithItems : SearchViewState()
    object ErrorNoConnectivity : SearchViewState()
    object ErrorNoConnectivityWithItems : SearchViewState()
    object DoneSearching : SearchViewState()
}

sealed class SearchViewStateV2 {
    object Searching : SearchViewStateV2()
    object ErrorUnknown : SearchViewStateV2()
    object ErrorNoConnectivity : SearchViewStateV2()
    data class DoneSearching(val listing: LiveData<PagedList<SearchResultItem>>) : SearchViewStateV2()
}

sealed class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    object MovieType : SearchResultTypeIcon(R.drawable.ic_clapperboard)
    object PersonType : SearchResultTypeIcon(R.drawable.ic_person_black)
}


data class SearchResultItem(val id: Double,
                            val imagePath: String,
                            val name: String,
                            val icon: SearchResultTypeIcon)
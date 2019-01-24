package com.jpp.moviespreview.screens.main.search

import androidx.annotation.DrawableRes
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

sealed class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    object MovieType : SearchResultTypeIcon(R.drawable.ic_clapperboard)
    object PersonType : SearchResultTypeIcon(R.drawable.ic_person_black)
}


data class SearchResultItem(val id: Double,
                            val imagePath: String,
                            val name: String,
                            val icon: SearchResultTypeIcon)
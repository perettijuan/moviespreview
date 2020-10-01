package com.jpp.mp.main.discover

import android.view.View
import com.jpp.mp.R

/**
 * Represents the view state that the filters section in the
 * discover movies feature can assume.
 */
data class DiscoverMoviesFiltersViewState(
    val visibility: Int = View.INVISIBLE,
    val isExpanded: Boolean = false,
    val discoverTitle: Int = R.string.discover_movies_filters
) {


    fun showVisible(): DiscoverMoviesFiltersViewState = copy(visibility = View.VISIBLE)

    companion object {
        fun showLoading(): DiscoverMoviesFiltersViewState = DiscoverMoviesFiltersViewState()
    }
}
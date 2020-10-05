package com.jpp.mp.main.discover

import android.view.View
import com.jpp.mp.R
import com.jpp.mp.main.discover.filters.genres.GenreFilterItem

/**
 * Represents the view state that the filters section in the
 * discover movies feature can assume.
 */
data class DiscoverMoviesFiltersViewState(
    val visibility: Int = View.GONE,
    val isExpanded: Boolean = false,
    val discoverTitle: Int = R.string.discover_movies_filters,
    val genreTitle: Int = R.string.discover_movies_genres_filter_title,
    val genreList: List<GenreFilterItem> = listOf(),
    val isApplyFilterEnabled: Boolean = false
) {


    fun showVisible(genreList: List<GenreFilterItem>): DiscoverMoviesFiltersViewState =
        copy(visibility = View.VISIBLE, genreList = genreList)

    fun updateSelectedState(item: GenreFilterItem): DiscoverMoviesFiltersViewState {
        val index = genreList.indexOf(item)
        val newList = genreList.toMutableList()
        if (index != -1) {
            newList[index] = item.copy(isSelected = !item.isSelected)
        }
        val applyEnabled = newList.count { updatedItem -> updatedItem.isSelected} > 0
        return copy(genreList = newList, isApplyFilterEnabled = applyEnabled)
    }

    fun updateToLoading(): DiscoverMoviesFiltersViewState = copy(visibility = View.INVISIBLE, isExpanded = false)
    fun refreshAfterLoading(): DiscoverMoviesFiltersViewState =
        copy(visibility = View.VISIBLE)

    companion object {
        fun showLoading(): DiscoverMoviesFiltersViewState = DiscoverMoviesFiltersViewState()
    }
}
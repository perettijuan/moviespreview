package com.jpp.mp.main.discover

import android.view.View
import com.jpp.mp.common.extensions.addList

/**
 * Represents the state of the content shown in the discover movies section.
 */
data class DiscoverMoviesContentViewState(
    val visibility: Int = View.INVISIBLE,
    val itemList: List<DiscoveredMovieListItem> = emptyList()
) {
    fun showItems(items: List<DiscoveredMovieListItem>): DiscoverMoviesContentViewState = copy(
        visibility = View.VISIBLE,
        itemList = itemList.toMutableList().addList(items)
    )
}

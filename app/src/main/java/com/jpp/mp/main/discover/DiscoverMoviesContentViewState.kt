package com.jpp.mp.main.discover

import android.view.View

/**
 * Represents the state of the content shown in the discover movies section.
 */
class DiscoverMoviesContentViewState(
    val visibility: Int = View.INVISIBLE,
    val itemList: List<DiscoveredMovieListItem> = emptyList()
)
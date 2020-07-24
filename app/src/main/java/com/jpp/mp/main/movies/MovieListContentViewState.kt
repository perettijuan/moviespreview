package com.jpp.mp.main.movies

import android.view.View

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class MovieListContentViewState(
    val visibility: Int = View.INVISIBLE,
    val movieList: List<MovieListItem> = emptyList()
)

package com.jpp.mp.main.movies

import android.view.View
import com.jpp.mp.common.extensions.addList

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class MovieListContentViewState(
    val visibility: Int = View.INVISIBLE,
    val movieList: List<MovieListItem> = emptyList()
) {

    fun showMovies(list: List<MovieListItem>): MovieListContentViewState = copy(
        visibility = View.VISIBLE,
        movieList = movieList.toMutableList().addList(list)
    )
}

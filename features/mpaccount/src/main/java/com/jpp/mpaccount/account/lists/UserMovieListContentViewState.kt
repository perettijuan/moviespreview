package com.jpp.mpaccount.account.lists

import android.view.View
import com.jpp.mp.common.extensions.addList

/**
 * Represents the view state of the content shown in the movie list view.
 */
internal data class UserMovieListContentViewState(
    val visibility: Int = View.INVISIBLE,
    val movieList: List<UserMovieItem> = emptyList()
) {
    fun showMovieList(movies: List<UserMovieItem>): UserMovieListContentViewState = copy(
        visibility = View.VISIBLE,
        movieList = movieList.toMutableList().addList(movies)
    )
}

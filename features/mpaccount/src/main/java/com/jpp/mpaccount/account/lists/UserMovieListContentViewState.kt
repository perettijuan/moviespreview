package com.jpp.mpaccount.account.lists

import android.view.View
import androidx.paging.PagedList

/**
 * Represents the view state of the content shown in the movie list view.
 */
internal data class UserMovieListContentViewState(
    val visibility: Int = View.INVISIBLE,
    val movieList: PagedList<UserMovieItem>? = null
)
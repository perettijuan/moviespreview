package com.jpp.mpaccount.account.lists

import android.view.View
import androidx.paging.PagedList
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the user movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
internal data class UserMovieListViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: UserMovieListContentViewState = UserMovieListContentViewState()
) {
    companion object {
        fun showLoading() = UserMovieListViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) =
            UserMovieListViewState(errorViewState = ErrorViewState.asUnknownError(errorHandler))

        fun showNoConnectivityError(errorHandler: () -> Unit) =
            UserMovieListViewState(errorViewState = ErrorViewState.asConnectivity(errorHandler))

        fun showMovieList(pagedList: PagedList<UserMovieItem>) = UserMovieListViewState(
            contentViewState = UserMovieListContentViewState(
                visibility = View.VISIBLE,
                movieList = pagedList
            )
        )
    }
}






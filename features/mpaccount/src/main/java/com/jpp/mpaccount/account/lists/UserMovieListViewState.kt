package com.jpp.mpaccount.account.lists

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the user movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
internal data class UserMovieListViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val screenTitle: Int = 0,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: UserMovieListContentViewState = UserMovieListContentViewState()
) {
    fun showMovieList(movieList: List<UserMovieItem>): UserMovieListViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            contentViewState = contentViewState.showMovieList(movieList),
            errorViewState = ErrorViewState.asNotVisible()
        )

    fun showNoConnectivityError(errorHandler: () -> Unit): UserMovieListViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

    fun showUnknownError(errorHandler: () -> Unit): UserMovieListViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

    companion object {
        fun showLoading(screenTitle: Int) =
            UserMovieListViewState(screenTitle = screenTitle, loadingVisibility = View.VISIBLE)
    }
}

package com.jpp.mpaccount.account

import android.view.View
import com.jpp.mpaccount.R
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state that the user account view can assume at any given moment.
 */
internal data class UserAccountViewState(
    val screenTitle: Int = R.string.account_title,
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: UserAccountContentViewState = UserAccountContentViewState()
) {
    companion object {
        fun showLoading() = UserAccountViewState(loadingVisibility = View.VISIBLE)
        fun showNoConnectivityError(errorHandler: () -> Unit) = UserAccountViewState(
            errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

        fun showUnknownError(errorHandler: () -> Unit) = UserAccountViewState(
            errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

        fun showContentWithAvatar(
            userName: String,
            accountName: String,
            favoriteMovieState: UserMoviesViewState,
            ratedMovieState: UserMoviesViewState,
            watchListState: UserMoviesViewState,
            avatarUrl: String,
            avatarCallback: (() -> Unit)
        ) = UserAccountViewState(
            contentViewState = UserAccountContentViewState.withAvatar(
                userName,
                accountName,
                favoriteMovieState,
                ratedMovieState,
                watchListState,
                avatarUrl,
                avatarCallback
            )
        )

        fun showContentWithLetter(
            userName: String,
            accountName: String,
            favoriteMovieState: UserMoviesViewState,
            ratedMovieState: UserMoviesViewState,
            watchListState: UserMoviesViewState,
            defaultLetter: String
        ) = UserAccountViewState(
            contentViewState = UserAccountContentViewState.withLetter(
                userName,
                accountName,
                favoriteMovieState,
                ratedMovieState,
                watchListState,
                defaultLetter
            )
        )
    }
}




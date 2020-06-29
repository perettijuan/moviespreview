package com.jpp.mpaccount.account

import android.view.View
import com.jpp.mpdesign.views.MPErrorView

/**
 * Represents the view state of the body section in the user account view.
 */
internal data class UserAccountBodyViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: MPErrorView.ErrorViewState = MPErrorView.ErrorViewState.asNotVisible(),
    val favoriteMovieState: UserMoviesViewState = UserMoviesViewState(),
    val ratedMovieState: UserMoviesViewState = UserMoviesViewState(),
    val watchListState: UserMoviesViewState = UserMoviesViewState()
) {

    fun showNoConnectivityError(errorHandler: () -> Unit): UserAccountBodyViewState = copy(
        loadingVisibility = View.INVISIBLE,
        errorViewState = MPErrorView.ErrorViewState.asConnectivity(errorHandler)
    )

    fun showUnknownError(errorHandler: () -> Unit): UserAccountBodyViewState = copy(
        loadingVisibility = View.INVISIBLE,
        errorViewState = MPErrorView.ErrorViewState.asUnknownError(errorHandler)
    )

    fun showContentWithMovies(
        favoriteMovieState: UserMoviesViewState,
        ratedMovieState: UserMoviesViewState,
        watchListState: UserMoviesViewState
    ) = UserAccountBodyViewState(
        loadingVisibility = View.INVISIBLE,
        favoriteMovieState = favoriteMovieState,
        ratedMovieState = ratedMovieState,
        watchListState = watchListState
    )

    companion object {
        fun showLoading(): UserAccountBodyViewState =
            UserAccountBodyViewState(loadingVisibility = View.VISIBLE)
    }
}

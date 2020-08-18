package com.jpp.mpmoviedetails

import android.view.View

internal data class MovieActionsViewState(
    val visibility: Int = View.INVISIBLE,
    val favoriteButtonState: ActionButtonState = ActionButtonState(),
    val watchListButtonState: ActionButtonState = ActionButtonState(),
    val rateImage: Int = R.drawable.ic_rate_filled,
    val creditsText: Int = R.string.movie_credits_title
) {

    fun showLoadedWithRating(
        favoriteButtonState: ActionButtonState,
        watchListButtonState: ActionButtonState
    ): MovieActionsViewState {
        return copy(
            visibility = View.VISIBLE,
            favoriteButtonState = favoriteButtonState,
            watchListButtonState = watchListButtonState,
            rateImage = R.drawable.ic_rate_filled
        )
    }

    fun showLoadedNoRating(
        favoriteButtonState: ActionButtonState,
        watchListButtonState: ActionButtonState
    ): MovieActionsViewState {
        return copy(
            visibility = View.VISIBLE,
            favoriteButtonState = favoriteButtonState,
            watchListButtonState = watchListButtonState,
            rateImage = R.drawable.ic_rate_empty
        )
    }

    companion object {
        fun showLoading() = MovieActionsViewState()
    }
}
package com.jpp.mpmoviedetails

import android.view.View

/**
 * Represents the view state that the action item in the movie detail can assume.
 * [animate] indicates if the actions section should perform the animation.
 * [expanded] indicates if the actions section should be shown expanded or not.
 */
internal data class MovieDetailActionViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val reloadButtonVisibility: Int = View.INVISIBLE,
    val actionButtonVisibility: Int = View.INVISIBLE,
    val rateButtonState: ActionButtonState = ActionButtonState(),
    val watchListButtonState: ActionButtonState = ActionButtonState(),
    val favoriteButtonState: ActionButtonState = ActionButtonState(),
    val errorState: ActionErrorViewState = ActionErrorViewState.None,
    val animate: Boolean = false,
    val expanded: Boolean = false
) {

    fun showLoaded(
        watchListButtonState: ActionButtonState,
        favoriteButtonState: ActionButtonState
    ): MovieDetailActionViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            reloadButtonVisibility = View.INVISIBLE,
            actionButtonVisibility = View.VISIBLE,
            rateButtonState = rateButtonState.asVisible(),
            watchListButtonState = watchListButtonState,
            favoriteButtonState = favoriteButtonState,
            errorState = ActionErrorViewState.None,
            animate = false,
            expanded = false
        )
    }

    fun showReload(): MovieDetailActionViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            reloadButtonVisibility = View.VISIBLE,
            actionButtonVisibility = View.INVISIBLE,
            rateButtonState = rateButtonState.asInVisible(),
            watchListButtonState = watchListButtonState.asInVisible(),
            favoriteButtonState = favoriteButtonState.asInVisible(),
            errorState = ActionErrorViewState.UnknownError,
            animate = false,
            expanded = false
        )
    }

    fun showLoadingFavorite(): MovieDetailActionViewState {
        return copy(
            favoriteButtonState = favoriteButtonState.asLoading()
        )
    }

    fun showLoadingWatchlist(): MovieDetailActionViewState {
        return copy(
            watchListButtonState = watchListButtonState.asLoading()
        )
    }

    fun showUserNotLogged(): MovieDetailActionViewState {
        return copy(
            loadingVisibility = View.INVISIBLE,
            reloadButtonVisibility = View.INVISIBLE,
            actionButtonVisibility = View.INVISIBLE, // IMPORTANT: this might be interpreted as a bug, but it is desired to hide the button.
            rateButtonState = rateButtonState.asInVisible(),
            watchListButtonState = watchListButtonState.asInVisible(),
            favoriteButtonState = favoriteButtonState.asInVisible(),
            errorState = ActionErrorViewState.UserNotLogged,
            animate = true,
            expanded = false
        )
    }

    companion object {
        fun showLoading(): MovieDetailActionViewState =
            MovieDetailActionViewState(loadingVisibility = View.VISIBLE)
    }
}

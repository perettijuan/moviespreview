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



    companion object {
        fun showLoading(): MovieDetailActionViewState =
            MovieDetailActionViewState(loadingVisibility = View.VISIBLE)
    }
}

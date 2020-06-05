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
    val errorState: ErrorState = ErrorState.None,
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
            errorState = ErrorState.None,
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
            errorState = ErrorState.UnknownError,
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
            actionButtonVisibility = View.INVISIBLE,
            rateButtonState = rateButtonState.asInVisible(),
            watchListButtonState = watchListButtonState.asInVisible(),
            favoriteButtonState = favoriteButtonState.asInVisible(),
            errorState = ErrorState.UserNotLogged,
            animate = true,
            expanded = false
        )
    }

    companion object {
        fun showLoading(): MovieDetailActionViewState =
            MovieDetailActionViewState(loadingVisibility = View.VISIBLE)
    }
}

/**
 * Represents the state of the action buttons show to the user.
 */
internal data class ActionButtonState(
    val visibility: Int = View.INVISIBLE,
    val animateLoading: Boolean = false,
    val asFilled: Boolean = false,
    val asClickable: Boolean = false
) {
    fun asVisible(): ActionButtonState {
        return copy(visibility = View.VISIBLE)
    }

    fun asInVisible(): ActionButtonState {
        return copy(visibility = View.VISIBLE)
    }

    fun asFilled(): ActionButtonState {
        return copy(
            visibility= View.VISIBLE,
            asFilled = true,
            asClickable = true,
            animateLoading = false
        )
    }

    fun asEmpty(): ActionButtonState {
        return copy(
            visibility= View.VISIBLE,
            asFilled = false,
            asClickable = true,
            animateLoading = false
        )
    }

    fun asLoading(): ActionButtonState {
        return copy(
            animateLoading = true,
            asClickable = false
        )
    }

    fun flipState(): ActionButtonState {
        return copy(
            animateLoading = false,
            asClickable = true,
            asFilled = !this.asFilled
        )
    }
}

/**
 * Represents the error states that the actions section can assume.
 */
internal sealed class ErrorState {
    object None : ErrorState()
    object UserNotLogged: ErrorState()
    object UnknownError : ErrorState()
}

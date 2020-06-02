package com.jpp.mpmoviedetails

/**
 * Represents the view state that the action item in the movie detail can assume.
 * [animate] indicates if the actions section should perform the animation.
 * [expanded] indicates if the actions section should be shown expanded or not.
 */
sealed class MovieDetailActionViewState(val animate: Boolean, val expanded: Boolean) {
    /*
     * Shows the loading state in the actions section.
     */
    object ShowLoading : MovieDetailActionViewState(animate = false, expanded = false)

    /*
     * Shows the view state when there's no movie state to render. i.e.: the user is
     * not logged.
     */
    data class ShowNoMovieState(
        val showActionsExpanded: Boolean,
        val animateActionsExpanded: Boolean
    ) :
        MovieDetailActionViewState(animate = animateActionsExpanded, expanded = showActionsExpanded)

    /*
     * Shows the view state when there's an error and the user needs to reload the data.
     */
    data class ShowReloadState(val animateActionsExpanded: Boolean) :
        MovieDetailActionViewState(animate = animateActionsExpanded, expanded = false)

    /*
     * Shows the user not logged view state.
     */
    data class ShowUserNotLogged(
        val showActionsExpanded: Boolean,
        val animateActionsExpanded: Boolean
    ) :
        MovieDetailActionViewState(animate = animateActionsExpanded, expanded = showActionsExpanded)

    /*
     * Renders the movie state with the provided data.
     */
    data class ShowMovieState(
        val showActionsExpanded: Boolean,
        val animateActionsExpanded: Boolean,
        val favorite: ActionButtonState,
        val isRated: Boolean,
        val isInWatchlist: ActionButtonState
    ) :
        MovieDetailActionViewState(animate = animateActionsExpanded, expanded = showActionsExpanded)
}

/**
 * Represents the state of the action buttons show to the user.
 */
sealed class ActionButtonState {
    object ShowAsFilled : ActionButtonState()
    object ShowAsEmpty : ActionButtonState()
    object ShowAsLoading : ActionButtonState()
}

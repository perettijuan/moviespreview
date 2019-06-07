package com.jpp.mp.screens.main.details

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jpp.mp.R

/**
 * Represents the view state of the details fragment.
 */
sealed class MovieDetailsViewState {
    object Loading : MovieDetailsViewState()
    object ErrorUnknown : MovieDetailsViewState()
    object ErrorNoConnectivity : MovieDetailsViewState()
}

/**
 * Represents the state that the movie actions section can assume at any given time.
 */
sealed class MovieActionsState {
    object Hidden : MovieActionsState()
    object UserNotLoggedIn : MovieActionsState()
    data class Shown(val isFavorite: Boolean) : MovieActionsState()
    data class Updating(val favorite: Boolean) : MovieActionsState()
}

/**
 * Represents the navigation event that can be routed through the details section.
 */
sealed class MovieDetailsNavigationEvent {
    object ToLogin : MovieDetailsNavigationEvent()
    data class ToCredits(val movieId: Double, val movieTitle: String) : MovieDetailsNavigationEvent()
}




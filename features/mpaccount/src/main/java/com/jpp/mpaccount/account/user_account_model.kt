package com.jpp.mpaccount.account

/*
 * Contains the definitions for the entire model used in the user account feature.
 */

/**
 * Represents the view states that the user account view can assume.
 */
sealed class UserAccountViewState {
    /*
     * Shows the not connected to network state
     */
    object NotConnected : UserAccountViewState()
    /*
     * Shown when the VM indicates that a work is in progress.
     */
    object Loading : UserAccountViewState()
}

/**
 * Represents all the navigation events that the user account view will response to.
 */
sealed class UserAccountNavigationEvent {
    /*
     * Used when the VM detects that the user is not logged in.
     */
    object GoToLogin : UserAccountNavigationEvent()
}
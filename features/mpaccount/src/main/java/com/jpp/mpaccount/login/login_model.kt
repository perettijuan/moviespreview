package com.jpp.mpaccount.login

/*
 * Contains the definitions for the entire model used in the login feature.
 */

/**
 * Represents the view states that the login view can assume.
 */
sealed class LoginViewState {
    /*
     * Shows the not connected to network state
     */
    object ShowNotConnected : LoginViewState()

    /*
     * Shown when the VM indicates that a work is in progress.
     */
    object ShowLoading : LoginViewState()

    /*
     * Shows a message indicating that it is impossible to login at this moment.
     */
    object ShowLoginError : LoginViewState()

    /*
     * Starts the oauth2 process to show the login UI to the user.
     */
    data class ShowOauth(val url: String,
                         val interceptUrl: String,
                         val reminder: Boolean = false) : LoginViewState()
}

/**
 * Represents all the navigation events that the login view will response to.
 */
sealed class LoginNavigationEvent {
    /*
     * Used when the user is logged in to dismiss the login view and go to user
     * account section.
     */
    object ContinueToUserAccount : LoginNavigationEvent()
}
package com.jpp.mpaccount.login

import com.jpp.mpdomain.AccessToken

/*
 * Contains the definitions for the entire model used in the login feature.
 */

/**
 * Represents the view states that the login view can assume.
 */
sealed class LoginViewState {
    /*
     * Shown when the VM indicates that a work is in progress.
     */
    object Loading : LoginViewState()

    /*
     * Shows a message indicating that it is impossible to login at this moment.
     */
    object UnableToLogin : LoginViewState()

    /*
     * Starts the oauth2 process to show the login UI to the user.
     * TODO JPP reminder - check AccountViewState.Oauth
     */
    data class ShowOauth(val url: String, val interceptUrl: String, val accessToken: AccessToken) : LoginViewState()
}

/**
 * Represents all the navigation events that the login view will response to.
 */
sealed class LoginNavigationEvent {
    /*
     * Used when the user is logged in to dismiss the login view and go back
     * to the previous step.
     */
    object BackToPrevious : LoginNavigationEvent()
}
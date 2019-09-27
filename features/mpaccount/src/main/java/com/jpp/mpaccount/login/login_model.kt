package com.jpp.mpaccount.login

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/*
 * This file contains the definitions for the entire model used in the login section.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view state of the login screen.
 */
data class LoginViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val oauthViewState: OauthViewState = OauthViewState()
) {
    companion object {
        fun showLoading() = LoginViewState(loadingVisibility = View.VISIBLE)
        fun showNoConnectivityError(errorHandler: () -> Unit) = LoginViewState(
                errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

        fun showUnknownError(errorHandler: () -> Unit) = LoginViewState(
                errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

        fun showOauth(url: String,
                      interceptUrl: String,
                      reminder: Boolean,
                      redirectListener: (String) -> Unit) = LoginViewState(
                oauthViewState = OauthViewState(
                        visibility = View.VISIBLE,
                        url = url,
                        interceptUrl = interceptUrl,
                        reminder = reminder,
                        redirectListener = redirectListener
                )
        )
    }
}

/**
 * Represents the view state of the Oauth section of the login screen.
 */
data class OauthViewState(
        val visibility: Int = View.INVISIBLE,
        val url: String? = null,
        val interceptUrl: String? = null,
        val redirectListener: ((String) -> Unit)? = null,
        val reminder: Boolean = false
)
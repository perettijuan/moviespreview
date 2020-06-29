package com.jpp.mpaccount.login

import android.view.View
import com.jpp.mpaccount.R
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the login screen.
 */
internal data class LoginViewState(
    val screenTitle: Int = R.string.login_generic,
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

        fun showOauth(
            url: String,
            interceptUrl: String,
            reminder: Boolean,
            redirectListener: (String) -> Unit
        ) = LoginViewState(
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

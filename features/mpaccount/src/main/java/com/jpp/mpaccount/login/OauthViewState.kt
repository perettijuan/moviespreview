package com.jpp.mpaccount.login

import android.view.View

/**
 * Represents the view state of the Oauth section of the login screen.
 */
internal data class OauthViewState(
    val visibility: Int = View.INVISIBLE,
    val url: String? = null,
    val interceptUrl: String? = null,
    val redirectListener: ((String) -> Unit)? = null,
    val reminder: Boolean = false
)

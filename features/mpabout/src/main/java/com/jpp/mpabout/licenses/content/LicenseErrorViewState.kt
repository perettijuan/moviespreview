package com.jpp.mpabout.licenses.content

import android.view.View

/**
 * Represents the state of the error view.
 */
internal data class LicenseErrorViewState(
    val visibility: Int = View.INVISIBLE,
    val isConnectivity: Boolean = false,
    val errorHandler: (() -> Unit)? = null
) {

    companion object {
        fun asUnknownError(handler: () -> Unit) = LicenseErrorViewState(
            visibility = View.VISIBLE,
            errorHandler = handler
        )
    }
}

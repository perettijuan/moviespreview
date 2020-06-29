package com.jpp.mpabout.licenses

import android.view.View

/**
 * Represents the state of the error view.
 */
internal data class LicensesErrorViewState(
    val visibility: Int = View.INVISIBLE,
    val isConnectivity: Boolean = false,
    val errorHandler: (() -> Unit)? = null
) {

    companion object {
        fun asUnknownError(handler: () -> Unit) = LicensesErrorViewState(
            visibility = View.VISIBLE,
            errorHandler = handler
        )
    }
}

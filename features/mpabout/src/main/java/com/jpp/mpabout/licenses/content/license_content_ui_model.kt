package com.jpp.mpabout.licenses.content

import android.view.View

data class LicenseContentViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: LicenseErrorViewState = LicenseErrorViewState(),
        val content: LicenseContent = LicenseContent()) {

    companion object {
        fun showLoading() = LicenseContentViewState(loadingVisibility = View.VISIBLE)
        fun showContent(url: String) = LicenseContentViewState(content = LicenseContent.withContent(url))
        fun showError(errorHandler: () -> Unit) = LicenseContentViewState(errorViewState = LicenseErrorViewState.asUnknownError(errorHandler))
    }

}


/**
 * Represents the state of the error view.
 */
data class LicenseErrorViewState(val visibility: Int = View.INVISIBLE,
                                 val isConnectivity: Boolean = false,
                                 val errorHandler: (() -> Unit)? = null) {

    companion object {
        fun asUnknownError(handler: () -> Unit) = LicenseErrorViewState(
                visibility = View.VISIBLE,
                errorHandler = handler)
    }
}

/**
 * Represents the content of the screen.
 */
data class LicenseContent(
        val visibility: Int = View.INVISIBLE,
        val licenseUrl: String = "") {
    companion object {
        fun withContent(url: String) =
                LicenseContent(
                        visibility = View.VISIBLE,
                        licenseUrl = url
                )
    }
}
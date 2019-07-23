package com.jpp.mpabout.licenses

import android.view.View

/**
 * Represents the view state of the licenses fragment.
 */
data class LicensesViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: LicensesErrorViewState = LicensesErrorViewState(),
        val content: LicensesContent = LicensesContent()) {

    companion object {
        fun showLoading() = LicensesViewState(loadingVisibility = View.VISIBLE)
        fun showContent(items: List<LicenseItem>) = LicensesViewState(content = LicensesContent.withContent(items))
        fun showError(errorHandler: () -> Unit) = LicensesViewState(errorViewState = LicensesErrorViewState.asUnknownError(errorHandler))
    }

}

/**
 * Represents the state of the error view.
 */
data class LicensesErrorViewState(val visibility: Int = View.INVISIBLE,
                                  val isConnectivity: Boolean = false,
                                  val errorHandler: (() -> Unit)? = null) {

    companion object {
        fun asUnknownError(handler: () -> Unit) = LicensesErrorViewState(
                visibility = View.VISIBLE,
                errorHandler = handler)
    }
}


/**
 * Represents the content of the screen.
 */
data class LicensesContent(
        val visibility: Int = View.INVISIBLE,
        val licenseItems: List<LicenseItem> = emptyList()
) {

    companion object {
        fun withContent(items: List<LicenseItem>) =
                LicensesContent(
                        visibility = View.VISIBLE,
                        licenseItems = items
                )
    }

}


/**
 * Represents an item in the list of results shown.
 */
data class LicenseItem(val id: Int,
                       val name: String)
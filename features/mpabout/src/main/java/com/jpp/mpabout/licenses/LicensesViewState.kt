package com.jpp.mpabout.licenses

import android.view.View
/**
 * Represents the view state of the licenses fragment.
 */
internal data class LicensesViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: LicensesErrorViewState = LicensesErrorViewState(),
    val content: LicensesContent = LicensesContent()
) {

    companion object {
        fun showLoading() = LicensesViewState(loadingVisibility = View.VISIBLE)
        fun showContent(items: List<LicenseItem>) =
            LicensesViewState(content = LicensesContent.withContent(items))

        fun showError(errorHandler: () -> Unit) =
            LicensesViewState(errorViewState = LicensesErrorViewState.asUnknownError(errorHandler))
    }
}

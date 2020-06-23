package com.jpp.mpabout.licenses

import android.view.View
import com.jpp.mpabout.R

/**
 * Represents the view state of the licenses fragment.
 */
internal data class LicensesViewState(
    val screenTitle: Int = R.string.about_open_source_action,
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: LicensesErrorViewState = LicensesErrorViewState(),
    val content: LicensesContent = LicensesContent()
) {

    fun showContent(items: List<LicenseItem>): LicensesViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            content = LicensesContent.withContent(items)
        )

    fun showError(errorHandler: () -> Unit): LicensesViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = LicensesErrorViewState.asUnknownError(errorHandler)
        )

    companion object {
        fun showLoading() = LicensesViewState(loadingVisibility = View.VISIBLE)
    }
}

package com.jpp.mpabout.licenses.content

import android.view.View

internal data class LicenseContentViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: LicenseErrorViewState = LicenseErrorViewState(),
    val content: LicenseContent = LicenseContent()
) {

    companion object {
        fun showLoading() = LicenseContentViewState(loadingVisibility = View.VISIBLE)
        fun showContent(url: String) = LicenseContentViewState(content = LicenseContent.withContent(url))
        fun showError(errorHandler: () -> Unit) = LicenseContentViewState(errorViewState = LicenseErrorViewState.asUnknownError(errorHandler))
    }
}

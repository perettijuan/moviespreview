package com.jpp.mpabout

import android.view.View

/**
 * Represents the view state of the about fragment.
 */
internal data class AboutViewState(
    val screenTitle: Int = R.string.about_top_bar_title,
    val loadingVisibility: Int = View.INVISIBLE,
    val header: AboutHeader = AboutHeader(),
    val content: AboutContent = AboutContent()
) {
    fun showContent(
        appVersion: String,
        aboutItems: List<AboutItem>
    ): AboutViewState = copy(
        loadingVisibility = View.INVISIBLE,
        header = AboutHeader.withHeader(appVersion),
        content = AboutContent.withContent(aboutItems)
    )

    companion object {
        fun showLoading(): AboutViewState = AboutViewState(loadingVisibility = View.VISIBLE)
    }
}

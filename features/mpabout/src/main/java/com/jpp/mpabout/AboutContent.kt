package com.jpp.mpabout

import android.view.View

/**
 * Represents the content of the about screen.
 */
internal data class AboutContent(
    val visibility: Int = View.INVISIBLE,
    val contentText: Int = R.string.about_content,
    val aboutItems: List<AboutItem> = emptyList()
) {
    companion object {
        fun withContent(aboutItems: List<AboutItem>) =
            AboutContent(
                visibility = View.VISIBLE,
                aboutItems = aboutItems
            )
    }
}

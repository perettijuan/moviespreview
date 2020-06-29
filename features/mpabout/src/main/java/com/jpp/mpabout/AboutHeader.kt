package com.jpp.mpabout

import android.view.View

/**
 * Represents the header section of the about screen.
 */
internal data class AboutHeader(
    val visibility: Int = View.INVISIBLE,
    val icon: Int = R.drawable.ic_launcher_round,
    val title: Int = R.string.app_name,
    val appVersion: String = ""
) {

    companion object {
        fun withHeader(appVersion: String) = AboutHeader(
            visibility = View.VISIBLE,
            appVersion = appVersion
        )
    }
}

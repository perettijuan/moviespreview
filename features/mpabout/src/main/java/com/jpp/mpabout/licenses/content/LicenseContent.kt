package com.jpp.mpabout.licenses.content

import android.view.View
/**
 * Represents the content of the screen.
 */
internal data class LicenseContent(
    val visibility: Int = View.INVISIBLE,
    val licenseUrl: String = ""
) {
    companion object {
        fun withContent(url: String) =
            LicenseContent(
                visibility = View.VISIBLE,
                licenseUrl = url
            )
    }
}

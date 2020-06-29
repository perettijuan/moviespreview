package com.jpp.mpabout.licenses

import android.view.View

/**
 * Represents the content of the screen.
 */
internal data class LicensesContent(
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

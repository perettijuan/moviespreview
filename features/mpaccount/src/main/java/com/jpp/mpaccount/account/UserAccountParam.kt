package com.jpp.mpaccount.account

import android.content.res.Resources
import com.jpp.mpaccount.R

internal data class UserAccountParam(
    val screenTitle: String,
    val posterSize: Int
) {
    companion object {
        fun create(resources: Resources, posterSize: Int) = UserAccountParam(
            screenTitle = resources.getString(R.string.account_title),
            posterSize = posterSize
        )
    }
}
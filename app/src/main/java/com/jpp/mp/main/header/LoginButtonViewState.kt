package com.jpp.mp.main.header

import android.view.View
import androidx.annotation.StringRes
import com.jpp.mp.R

/**
 * Represents the view state of the login button.
 */
internal data class LoginButtonViewState(
    val visibility: Int = View.GONE,
    @StringRes val title: Int = R.string.nav_header_login
)

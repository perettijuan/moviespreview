package com.jpp.mpdesign.ext

import android.view.View
import androidx.annotation.StringRes

/**
 * Extension function to retrieve a String from the appModule resources.
 */
fun View.getStringFromResources(@StringRes stringResId: Int): CharSequence = resources.getString(stringResId)
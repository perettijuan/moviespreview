package com.jpp.mpdesign.ext

import android.view.View
import androidx.annotation.StringRes

/**
 * Extension function to retrieve a String from the appModule resources.
 */
fun View.getStringFromResources(@StringRes stringResId: Int): CharSequence = resources.getString(stringResId)

/**
 * Extension function for the View class to make a View visible
 */
fun View.setVisible() {
    this.visibility = View.VISIBLE
}

/**
 * Extension function for the View class to make a View visible when [condition] is true.
 * If [condition] is false, the [defVisibility] is applied.
 */
fun View.setVisibleWhen(condition: Boolean, defVisibility: Int = View.GONE) {
    this.visibility = if (condition) View.VISIBLE else defVisibility
}

/**
 * Extension function for the View class to make a View gone
 */
fun View.setGone() {
    this.visibility = View.GONE
}

/**
 * Extension function for the View class to make a View invisible
 */
fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}
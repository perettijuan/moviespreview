package com.jpp.moviespreview.ext

import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Extension function for the View class to make a View visible
 */
fun View.setVisible() {
    this.visibility = View.VISIBLE
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

/**
 * Extension function for the View class to retrieve a color given its resource identifier
 */
fun View.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)
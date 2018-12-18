package com.jpp.moviespreview.ext

import android.graphics.Point
import androidx.fragment.app.Fragment

/**
 * Returns a [Point] in which the x value represents the width of the screen in pixels
 * and the y values represents the height of the screen in pixels.
 */
fun Fragment.getScreenSizeInPixels(): Point {
    activity?.let {
        return it.getScreenSizeInPixels()
    } ?: kotlin.run {
        throw IllegalStateException("Activity is null at this point")
    }
}
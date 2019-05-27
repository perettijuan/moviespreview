package com.jpp.mp.common.extensions

import android.graphics.Point
import androidx.fragment.app.Fragment

/**
 * Returns a [Point] in which the x value represents the width of the screen in pixels
 * and the y values represents the height of the screen in pixels.
 */
fun Fragment.getScreenSizeInPixels(): Point {
    activity?.let {
        return it.getScreenSizeInPixels()
    } ?: throw IllegalStateException("Activity is null at this point")
}

/**
 * Returns an integer that represents the width of the screen in pixels.
 */
fun Fragment.getScreenWithInPixels(): Int {
    activity?.let {
        return it.getScreenSizeInPixels().x
    } ?: throw IllegalStateException("Activity is null at this point")
}
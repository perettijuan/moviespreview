package com.jpp.moviespreview.ext

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.WindowManager

/**
 * Returns a [Point] in which the x value represents the width of the screen in pixels
 * and the y values represents the height of the screen in pixels.
 */
fun Activity.getScreenSizeInPixels(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = android.graphics.Point()
    display.getSize(size)
    return size
}
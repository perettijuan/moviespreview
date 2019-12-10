package com.jpp.mp.common.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Returns a [Point] in which the x value represents the width of the screen in pixels
 * and the y values represents the height of the screen in pixels.
 */
fun Activity.getScreenSizeInPixels(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

/**
 * Extension function to find a ViewModel in an Activity.
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
    return androidx.lifecycle.ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

/**
 * Extension function to simplify the access to a ViewModel from an Activity.
 */
inline fun <reified T : ViewModel> FragmentActivity.withViewModel(viewModelFactory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = getViewModel<T>(viewModelFactory)
    vm.body()
    return vm
}

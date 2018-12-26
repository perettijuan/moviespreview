package com.jpp.moviespreview.ext

import android.graphics.Point
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

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

/**
 * Extension function to find a ViewModel in the Activity of the Fragment.
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
    return activity?.run {
        ViewModelProviders.of(this, viewModelFactory)[T::class.java]
    } ?: throw RuntimeException("Invalid Activity")
}

/**
 * Extension function to simplify the access to a ViewModel backed by the Fragment's
 * Activity.
 */
inline fun <reified T : ViewModel> Fragment.withViewModel(viewModelFactory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = getViewModel<T>(viewModelFactory)
    vm.body()
    return vm
}
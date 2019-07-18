package com.jpp.mp.common.extensions

import android.graphics.Point
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jpp.mp.common.navigation.NavigationViewModel

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
fun Fragment.getScreenWidthInPixels(): Int {
    activity?.let {
        return it.getScreenSizeInPixels().x
    } ?: throw IllegalStateException("Activity is null at this point")
}

/**
 * Retrieves the identifier of a given attribute defined in the Activity's theme.
 */
fun Fragment.getResIdFromAttribute(attr: Int): Int {
    val tp = android.util.TypedValue()
    activity?.theme?.resolveAttribute(attr, tp, true)
    return tp.resourceId
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

/**
 * Extension function to execute a particular action with the [NavigationViewModel].
 */
fun Fragment.withNavigationViewModel(viewModelFactory: ViewModelProvider.Factory, action: NavigationViewModel.() -> Unit) = getViewModel<NavigationViewModel>(viewModelFactory).action()
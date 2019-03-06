package com.jpp.moviespreview.ext

import android.graphics.Point
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.jpp.moviespreview.R

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
 * Retrieves the identifier of a given attribute defined in the Activity's theme.
 */
fun Fragment.getResIdFromAttribute(attr: Int): Int {
    val tp = android.util.TypedValue()
    activity?.theme?.resolveAttribute(attr, tp, true)
    return tp.resourceId
}

/**
 * Creates and shows a [Snackbar] styled with the application resources.
 */
fun Fragment.snackBar(contentView: View,
                      @StringRes message: Int,
                      @StringRes actionMessage: Int,
                      action: () -> Unit) {
    activity?.let {
        Snackbar.make(
                contentView,
                message,
                Snackbar.LENGTH_INDEFINITE
        ).setAction(actionMessage) {
            action.invoke()
        }.apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface = ResourcesCompat.getFont(it, R.font.poppins_bold)
            setActionTextColor(ContextCompat.getColor(it, R.color.primaryColor))
        }.show()
    }
}

/**
 * Finds the View identified with the provided [id].
 */
inline fun <reified T : View> Fragment.findViewById(@IdRes id: Int): T {
    return activity?.findViewById(id) ?: run {
        throw IllegalStateException("Activity nor present yet")
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
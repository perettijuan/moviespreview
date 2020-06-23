package com.jpp.mp.common.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Returns an integer that represents the width of the screen in pixels.
 */
fun Fragment.getScreenWidthInPixels(): Int {
    activity?.let {
        return it.getScreenSizeInPixels().x
    } ?: throw IllegalStateException("Activity is null at this øpoint")
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
        ViewModelProvider(this, viewModelFactory)[T::class.java]
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
 * Changes the title of the screen to the new provided [title].
 */
fun Fragment.setScreenTitle(title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}

/**
 * Grabs a value from the bundle extras or throws if it either
 * A) Cannot find that value and T is not nullable
 * B) The value provided is not of type T
 * If a default value is provided then it will not throw if it cannot find the value and will return
 * the default
 *
 * @param key the key inside the bundle extras
 * @param default optional value for if the value does not exist inside the bundle
 */
inline fun <reified T : Any> Fragment.getFragmentArgument(key: String, default: T? = null): T {
    val isNullable = null is T
    var value = arguments?.get(key)
    if (value == null || value !is T) {
        value = default
    }
    if (value == null && !isNullable) {
        throw NullPointerException("Unable to get $key from bundle argument")
    }
    return value as T
}

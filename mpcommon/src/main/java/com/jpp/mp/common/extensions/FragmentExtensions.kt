package com.jpp.mp.common.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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

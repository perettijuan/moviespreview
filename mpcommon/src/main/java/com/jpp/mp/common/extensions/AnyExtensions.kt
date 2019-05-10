package com.jpp.mp.common.extensions

import android.util.Log

/**
 * Extension function to execute a method over an object of type T and return the
 * original object plus the result of the function executed on it.
 */
inline fun <T, R> T.and(andFunction: (T) -> R): Pair<T, R> {
    return Pair(this, andFunction(this))
}

/**
 * Super simple function to log a message to console from anywhere.
 */
fun Any.log(text: String) {
    Log.d("SACACHISPAS", text)
}
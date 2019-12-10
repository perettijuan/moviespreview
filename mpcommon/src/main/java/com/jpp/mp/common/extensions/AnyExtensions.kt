package com.jpp.mp.common.extensions

import android.util.Log
import com.jpp.mp.common.BuildConfig

/**
 * Extension function to execute a method over an object of type T and return the
 * original object plus the result of the function executed on it.
 */
inline fun <T, R> T.and(andFunction: (T) -> R): Pair<T, R> {
    return Pair(this, andFunction(this))
}

/**
 * Logs the thread in witch the class is running.
 */
fun Any.logYourThread() {
    if (BuildConfig.DEBUG) {
        Log.d("LogYourThread", "Class [$javaClass.kotlin] running on thread with ID " + Thread.currentThread().id)
    }
}

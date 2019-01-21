package com.jpp.moviespreview.common.extensions

/**
 * Extension function to execute a method over an object of type T and return the
 * original object plus the result of the function executed on it.
 */
inline fun <T, R> T.and(andFunction: (T) -> R): Pair<T, R> {
    return Pair(this, andFunction(this))
}
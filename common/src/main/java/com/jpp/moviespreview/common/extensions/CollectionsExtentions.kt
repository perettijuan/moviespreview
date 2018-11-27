package com.jpp.moviespreview.common.extensions

/**
 * Returns a list in which the result of [mapper] is added to the end
 * of this list.
 */
inline fun<T> MutableList<T>.addAllMapping(mapper: () -> List<T>) : List<T> {
    addAll(mapper.invoke())
    return this
}
package com.jpp.mp.common.extensions

/**
 * Returns a list in which the result of [mapper] is added to the end
 * of this list.
 */
inline fun <T> MutableList<T>.addAllMapping(mapper: () -> List<T>): List<T> {
    addAll(mapper.invoke())
    return this
}

/**
 * Adds all the provided elements in [toAdd] to this list and return
 * the the whole list.
 */
fun <T> MutableList<T>.addList(toAdd: List<T>): List<T> {
    addAll(toAdd)
    return this
}

package com.jpp.mp.common.extensions

import java.util.*

/**
 * Transforms a String into it's integer representation by removing all
 * existing letters.
 * If the string does not contains Ints, null is returned.
 */
fun String.transformToInt(): Int? {
    if (!this.matches(".*\\d.*".toRegex())) {
        // string does not has numbers
        return null
    }

    // extract integer part
    val scanner = Scanner(this).useDelimiter("[^0-9]+")
    return scanner.nextInt()
}
package com.jpp.mp.common.extensions

import android.os.Bundle

/**
 * Finds the provided [key] in the Bundle. If the key does not
 * exists (or the Bundle is null), [default] String is returned.
 */
fun Bundle?.getStringOrDefault(key: String, default: String = ""): String {
    return this?.let { getString(key) } ?: default
}

fun Bundle?.getStringOrFail(key: String) : String {
    return this?.let { getString(key) } ?: throw IllegalStateException("Can't find the provided $key in this Bundle")
}
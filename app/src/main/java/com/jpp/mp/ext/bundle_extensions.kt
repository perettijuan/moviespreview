package com.jpp.mp.ext

import android.os.Bundle

/**
 * Finds the provided [key] in the Bundle. If the key does not
 * exists (or the Bundle is null), [default] String is returned.
 */
fun Bundle?.getStringOrDefault(key: String, default: String = ""): String {
    return this?.let { getString(key) } ?: default
}
package com.jpp.mp.common.extensions

import android.os.Bundle

fun Bundle?.getStringOrFail(key: String): String {
    return this?.let { getString(key) } ?: throw IllegalStateException("Can't find the provided $key in this Bundle")
}

fun Bundle?.getDoubleOrFail(key: String): Double {
    return this?.let { getDouble(key) } ?: throw IllegalStateException("Can't find the provided $key in this Bundle")
}

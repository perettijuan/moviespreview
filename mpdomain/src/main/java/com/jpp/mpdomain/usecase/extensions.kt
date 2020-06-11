package com.jpp.mpdomain.usecase

/**
 * Extension to create a URL from a String.
 */
internal fun String?.createUrlForPath(baseUrl: String, size: String): String? {
    return this.let {
        StringBuilder()
            .append(baseUrl)
            .append(size)
            .append(it)
            .toString()
    }
}
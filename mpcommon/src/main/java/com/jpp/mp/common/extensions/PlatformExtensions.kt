package com.jpp.mp.common.extensions

import android.os.Build
import android.webkit.CookieManager

fun CookieManager.clearAllCookies() {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        removeAllCookies(null)
    } else {
        removeAllCookie()
    }
}
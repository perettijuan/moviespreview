package com.jpp.mp.common.extensions

import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.Navigator

fun CookieManager.clearAllCookies() {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        removeAllCookies(null)
    } else {
        removeAllCookie()
    }
}

fun NavController.navigate(
    @IdRes resId: Int,
    args: Bundle,
    navigatorExtras: Navigator.Extras
) {
    this.navigate(resId, args, null, navigatorExtras)
}

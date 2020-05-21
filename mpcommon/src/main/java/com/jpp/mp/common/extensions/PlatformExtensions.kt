package com.jpp.mp.common.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.Navigator

fun NavController.navigate(
    @IdRes resId: Int,
    args: Bundle,
    navigatorExtras: Navigator.Extras
) {
    this.navigate(resId, args, null, navigatorExtras)
}

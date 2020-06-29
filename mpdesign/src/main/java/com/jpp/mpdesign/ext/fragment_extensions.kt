package com.jpp.mpdesign.ext

import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jpp.mpdesign.R

/**
 * Creates and shows a [Snackbar] styled with the application resources.
 */
fun Fragment.snackBar(
    contentView: View,
    @StringRes message: Int,
    @StringRes actionMessage: Int,
    action: () -> Unit
) {
    activity?.let {
        Snackbar.make(
                contentView,
                message,
                Snackbar.LENGTH_INDEFINITE
        ).setAction(actionMessage) {
            action.invoke()
        }.apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface = ResourcesCompat.getFont(it, R.font.poppins_bold)
            setActionTextColor(ContextCompat.getColor(it, R.color.primaryColor))
        }.show()
    }
}

/**
 * Creates and shows a [Snackbar] styled with the application resources.
 */
fun Fragment.snackBarNoAction(
    contentView: View?,
    @StringRes message: Int
) {
    if (contentView == null) return
    activity?.let {
        Snackbar.make(
                contentView,
                message,
                Snackbar.LENGTH_LONG
        ).apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface = ResourcesCompat.getFont(it, R.font.poppins_bold)
        }.show()
    }
}

fun Fragment.mpToast(@StringRes messageRes: Int) {
    Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT)
            .apply {
                view.background.setColorFilter(getColor(android.R.color.background_dark), PorterDuff.Mode.SRC_IN)
                view.findViewById<TextView>(android.R.id.message).setTextColor(getColor(android.R.color.white))
            }.show()
}

/**
 * Retrieves a color from the provided [colorRes].
 */
fun Fragment.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(requireContext(), colorRes)

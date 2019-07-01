package com.jpp.mpdesign.ext

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jpp.mpdesign.R


/**
 * Tints the background of the ViewGroup with a linear gradient constructed from the color
 * palette that can be fetch from the provided [bitmap].
 */
fun Fragment.tintBackgroundWithBitmap(bitmap: Bitmap) {
    (view as ViewGroup).tintBackgroundWithBitmap(bitmap)
}

/**
 * Tints the background of the ViewGroup with a linear gradient constructed from the [color]
 * provided.
 */
fun Fragment.tintBackgroundFromColor(@ColorRes color: Int) {
    (view as ViewGroup).tintBackgroundFromColor(color)
}

/**
 * Creates and shows a [Snackbar] styled with the application resources.
 */
fun Fragment.snackBar(contentView: View,
                      @StringRes message: Int,
                      @StringRes actionMessage: Int,
                      action: () -> Unit) {
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
fun Fragment.snackBarNoAction(contentView: View,
                              @StringRes message: Int) {
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
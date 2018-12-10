package com.jpp.moviespreview.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Extension function for the View class to make a View visible
 */
fun View.setVisible() {
    this.visibility = View.VISIBLE
}

/**
 * Extension function for the View class to make a View gone
 */
fun View.setGone() {
    this.visibility = View.GONE
}

/**
 * Extension function for the View class to make a View invisible
 */
fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

/**
 * Extension function for the View class to retrieve a color given its resource identifier
 */
fun View.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

/**
 * Extension function that modifies the alpha property of the view to set it to zero.
 */
fun View.toZeroAlpha() {
    alpha = 0F
}

/**
 * Extension function that modifies the alpha property of the view to set it to one.
 */
fun View.toOneAlpha() {
    alpha = 1F
}

/**
 * Extension function to retrieve a String from the appModule resources.
 */
fun View.getStringFromResources(@StringRes stringResId: Int) = resources.getString(stringResId)

/**
 * Animates the from the current alpha property value to alpha 1.
 */
fun View.animateToOneAlpha(startDelay: Long = 100,
                           duration: Long = 200,
                           callback: (() -> Unit)? = null) {

    animateToAlpha(1F, startDelay, duration, callback)
}

/**
 * Animates the from the current alpha property value to alpha 0.
 */
fun View.animateToZeroAlpha(startDelay: Long = 100,
                            duration: Long = 200,
                            callback: (() -> Unit)? = null) {
    animateToAlpha(0F, startDelay, duration, callback)
}

/**
 * Animates the from the current alpha property value to alpha [toAlpha].
 */
fun View.animateToAlpha(toAlpha: Float,
                        startDelay: Long = 100,
                        duration: Long = 200,
                        callback: (() -> Unit)? = null) {

    animate()
            .alpha(toAlpha)
            .setStartDelay(startDelay)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    callback?.invoke()
                }
            })
            .start()
}

/**
 * Loads an image retrieved from the provided [imageUrl]
 * into the ImageView.
 */
fun ImageView.loadImageUrl(imageUrl: String) {
    Picasso
            .with(context)
            .load(imageUrl)
            .into(this, object : Callback {
                override fun onSuccess() {
                    Log.d("JPPLOG", "Loaded -> $imageUrl")
                }

                override fun onError() {
                    Log.d("JPPLOG", "ERROR -> $imageUrl")
                }
            })
}



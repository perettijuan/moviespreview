package com.jpp.moviespreview.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.jpp.moviespreview.R
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


fun ImageView.clearImage(@DrawableRes placeholderRes: Int = R.drawable.ic_app_icon_black) {
    setImageResource(placeholderRes)
}

/**
 * Loads an image retrieved from the provided [imageUrl]
 * into the ImageView.
 */
fun ImageView.loadImageUrl(imageUrl: String,
                           @DrawableRes placeholderRes: Int = R.drawable.ic_app_icon_black,
                           @DrawableRes errorImageRes: Int = R.drawable.ic_error_black) {
    Picasso
            .with(context)
            .load(imageUrl)
            .fit()
            .centerCrop()
            .placeholder(placeholderRes)
            .error(errorImageRes)
            .into(this)
}

/**
 * Loads an image retrieved from the provided [imageUrl]
 * into the ImageView as a circular image.
 */
fun ImageView.loadImageUrlAsCircular(imageUrl: String,
                                     @DrawableRes placeholderRes: Int = R.drawable.ic_app_icon_black,
                                     @DrawableRes errorImageRes: Int = R.drawable.ic_error_black) {
    Picasso
            .with(context)
            .load(imageUrl)
            .fit()
            .centerCrop()
            .placeholder(placeholderRes)
            .error(errorImageRes)
            .into(this, object : Callback {
                override fun onSuccess() {
                    val imageAsBitmap = (drawable as BitmapDrawable).bitmap
                    val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, imageAsBitmap)
                    roundedBitmapDrawable.isCircular = true
                    roundedBitmapDrawable.cornerRadius = Math.max(imageAsBitmap.width.toDouble(), imageAsBitmap.height / 2.0).toFloat()
                    setImageDrawable(roundedBitmapDrawable)
                }

                override fun onError() {
                    //no-op
                }
            })

}

/**
 * Enables the title shown by the CollapsingToolbarLayout
 */
fun CollapsingToolbarLayout.enableTitle() {
    isTitleEnabled = true
}


/**
 * Disables the title shown by the CollapsingToolbarLayout
 */
fun CollapsingToolbarLayout.disableTitle() {
    isTitleEnabled = false
}
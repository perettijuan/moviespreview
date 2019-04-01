package com.jpp.mp.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.jpp.mp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


/**
 * Inflates a given layout resources and returns the inflated view.
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

/**
 * Extension function for the View class to make a View visible
 */
fun View.setVisible() {
    this.visibility = View.VISIBLE
}

/**
 * Extension function for the View class to make a View visible when [condition] is true.
 * If [condition] is false, the [defVisibility] is applied.
 */
fun View.setVisibleWhen(condition: Boolean, defVisibility: Int = View.GONE) {
    this.visibility = if (condition) View.VISIBLE else defVisibility
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
 * Extension function to retrieve a String from the appModule resources.
 */
fun View.getStringFromResources(@StringRes stringResId: Int): CharSequence = resources.getString(stringResId)

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
                                     @DrawableRes errorImageRes: Int = R.drawable.ic_error_black,
                                     onErrorAction: (() -> Unit)? = null) {
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
                    onErrorAction?.invoke()
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

/**
 * Retrieves the [CharSequence] representation of [textRes]
 */
fun View.getText(@StringRes textRes: Int): CharSequence {
    return resources.getText(textRes)
}

/**
 * Sets the text appearance of the TextView based on the current API level.
 */
fun TextView.setTextAppearanceCompat(@StyleRes resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

/**
 * Closes the drawer if it is open (using the START margin to determinate if is open).
 */
fun DrawerLayout.closeDrawerIfOpen() {
    if (isDrawerOpen(GravityCompat.START)) {
        closeDrawer(GravityCompat.START)
    }
}
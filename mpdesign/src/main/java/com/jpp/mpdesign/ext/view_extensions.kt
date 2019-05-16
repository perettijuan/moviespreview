package com.jpp.mpdesign.ext

import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.jpp.mpdesign.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Extension function to retrieve a String from the appModule resources.
 */
fun View.getStringFromResources(@StringRes stringResId: Int): CharSequence = resources.getString(stringResId)

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
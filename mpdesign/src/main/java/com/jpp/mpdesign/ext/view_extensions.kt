package com.jpp.mpdesign.ext

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
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
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpdesign.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


/**
 * Inflates a given layout resources and returns the inflated view.
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

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
                                     onErrorAction: (() -> Unit)? = null,
                                     onBitmapAvailable: ((Bitmap) -> Unit)? = null,
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
                    onBitmapAvailable?.invoke(imageAsBitmap)
                }

                override fun onError() {
                    onErrorAction?.invoke()
                }
            })

}

/**
 * Tints the background of the ViewGroup with a linear gradient constructed from the color
 * palette that can be fetch from the provided [bitmap].
 */
fun ViewGroup.tintBackgroundWithBitmap(bitmap: Bitmap) {
    Palette.from(bitmap).generate { palette ->
        palette?.withMostPopulous {
            background = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(ContextCompat.getColor(context, android.R.color.white), it.rgb)
            )
        }
    }
}

/**
 * Tints the background of the ViewGroup with a linear gradient constructed from the [color]
 * provided.
 */
fun ViewGroup.tintBackgroundFromColor(@ColorRes color: Int) {
    background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(ContextCompat.getColor(context, android.R.color.white), color)
    )
}

/**
 * Executes the [callback] with the most populous color found in the Palette.
 */
private fun Palette.withMostPopulous(callback: (Palette.Swatch) -> Unit) {
    var mostPopulous: Palette.Swatch? = null
    for (swatch in swatches) {
        if (mostPopulous == null || swatch.population > mostPopulous.population) {
            mostPopulous = swatch
        }
    }
    mostPopulous?.let(callback)
}

/**
 * Closes the drawer if it is expanded (using the START margin to determinate if is expanded).
 */
fun DrawerLayout.closeDrawerIfOpen() {
    if (isDrawerOpen(GravityCompat.START)) {
        closeDrawer(GravityCompat.START)
    }
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
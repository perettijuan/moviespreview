package com.jpp.mpdesign.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.jpp.mpdesign.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Custom [AppCompatImageView] used to wire data models with the UI using Android Data Binding.
 * This ImageView provides methods to set and download images from a remote resource when a
 * new URL is provided.
 */
class MPImageView : AppCompatImageView {

    private var circularURLErrorCallback: (() -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Downloads the image from the provided [url] and adapts it to be shown as circular
     * content of this ImageView. If the error case needs to be detected, provide
     * a callback using [circularUrlErrorCallback].
     */
    fun circularUrl(url: String?) {
        if (url == null) return
        loadImageUrlAsCircular(url, {
            circularURLErrorCallback?.invoke()
        })
    }

    /**
     * Provide a [callback] to be used when the image provided in [circularUrl] can
     * not be downloaded.
     */
    fun circularUrlErrorCallback(callback: (() -> Unit)?) {
        circularURLErrorCallback = callback
    }

    /**
     * Downloads the image from the provided [url] and adapts it to be shown as
     * content of this ImageView.
     */
    fun imageUrl(url: String) {
        loadImageUrl(url)
    }

    /**
     * Set the image identified with [imageRes] as content of this
     * ImageView.
     */
    fun imageRes(imageRes: Int) {
        setImageResource(imageRes)
    }

    /**
     * Loads an image retrieved from the provided [imageUrl]
     * into the ImageView as a circular image.
     */
    private fun loadImageUrlAsCircular(imageUrl: String,
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
     * Loads an image retrieved from the provided [imageUrl]
     * into the ImageView.
     */
    private fun loadImageUrl(imageUrl: String,
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
}
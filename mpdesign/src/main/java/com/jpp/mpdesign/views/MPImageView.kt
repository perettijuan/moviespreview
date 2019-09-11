package com.jpp.mpdesign.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.jpp.mpdesign.ext.loadImageUrl
import com.jpp.mpdesign.ext.loadImageUrlAsCircular

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
        loadImageUrlAsCircular(url, circularURLErrorCallback)
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
}
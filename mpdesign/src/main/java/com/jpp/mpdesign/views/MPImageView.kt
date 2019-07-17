package com.jpp.mpdesign.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.jpp.mpdesign.ext.loadImageUrlAsCircular

/**
 * Custom ImageView used mostly to allow loading URLs into the ImageView.
 */
class MPImageView : ImageView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun circularUrl(url: String) {
        loadImageUrlAsCircular(url)
    }
}
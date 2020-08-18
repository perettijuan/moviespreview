package com.jpp.mpdesign.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A custom [FloatingActionButton] implementation.
 */
class MPFloatingActionButton : FloatingActionButton {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun imageRes(@DrawableRes imageRes: Int) {
        setImageResource(imageRes)
    }
}

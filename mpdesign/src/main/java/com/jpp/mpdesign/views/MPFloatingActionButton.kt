package com.jpp.mpdesign.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jpp.mpdesign.R

/**
 * A custom [FloatingActionButton] that renders a filled or empty icon, depending on the configuration
 * of the view.
 *
 * Starts as empty by default.
 * Call asFilled/asEmpty to change the state of the icon.
 * Call asClickable/asNonClickable to change the clickable state of the button.
 * Call doAnimation to perform a simple rotating animation.
 */
class MPFloatingActionButton : FloatingActionButton {

    private var filledIcon: Drawable? = null
    private var emptyIcon: Drawable? = null

    private var isRotated: Boolean = false
    private var isAnimating: Boolean = false
    private var isAsClickable: Boolean = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        with(context.obtainStyledAttributes(attrs, R.styleable.MPFloatingActionButton)) {
            filledIcon = getDrawable(R.styleable.MPFloatingActionButton_fillIcon)
            emptyIcon = getDrawable(R.styleable.MPFloatingActionButton_emptyIcon)
            recycle()
        }

        setImageDrawable(emptyIcon)
    }

    fun setFilled(filled: Boolean) {
        if (filled) {
            setImageDrawable(filledIcon)
        } else {
            setImageDrawable(emptyIcon)
        }
    }

    fun setAsClickable(clickable: Boolean) {
        isAsClickable = clickable
        syncClickableState()
    }

    fun doAnimation(loading: Boolean) {
        if (!loading) {
            return
        }

        val degrees = if (isRotated) {
            isRotated = false
            0F
        } else {
            isRotated = true
            360F
        }

        animate()
            .rotation(degrees)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    isAnimating = false
                    syncClickableState()
                }

                override fun onAnimationStart(animation: Animator?) {
                    isAnimating = true
                    syncClickableState()
                }
            })
            .start()
    }

    private fun syncClickableState() {
        isClickable = !isAnimating && isAsClickable
    }
}

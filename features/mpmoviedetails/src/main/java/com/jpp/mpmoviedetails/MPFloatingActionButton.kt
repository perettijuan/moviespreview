package com.jpp.mpmoviedetails

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A custom [FloatingActionButton] that renders the actions that the user can take in a movie detail.
 * Starts as empty by default.
 * Call asFilled/asEmpty to change the state of the icon.
 * Call asClickable/asNonClickable to change the clickable state of the button.
 * Call doAnimation to perform a simple rotating animation.
 */
class MPFloatingActionButton : FloatingActionButton {

    private var filledIcon: Drawable? = null
    private var emptyIcon: Drawable? = null

    private var isAnimating: Boolean = false
    private var isAsClickable: Boolean = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.MPFloatingActionButton)) {
            filledIcon = getDrawable(R.styleable.MPFloatingActionButton_fillIcon)
            emptyIcon = getDrawable(R.styleable.MPFloatingActionButton_emptyIcon)
            recycle()
        }

        setImageDrawable(emptyIcon)
    }


    fun asFilled() {
        setImageDrawable(filledIcon)
    }

    fun asEmpty() {
        setImageDrawable(emptyIcon)
    }

    fun asClickable() {
        isAsClickable = true
        syncClickableState()
    }

    fun asNonClickable() {
        isAsClickable = false
        syncClickableState()
    }

    fun doAnimation() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.action_button_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                // no-op
            }

            override fun onAnimationEnd(animation: Animation?) {
                isAnimating = false
                syncClickableState()
            }

            override fun onAnimationStart(animation: Animation?) {
                isAnimating = true
                syncClickableState()
            }

        })
        startAnimation(animation)
    }

    private fun syncClickableState() {
        isClickable = !isAnimating && isAsClickable
    }
}


package com.jpp.mp.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.mp.R
import kotlinx.android.synthetic.main.layout_mp_action_button.view.*

/**
 * A custom view that renders the actions that the user can take in MP.
 * Starts as empty by default.
 * Call asFilled/asEmpty to change the state of the icon.
 * Call asClickable/asNonClickable to change the clickable state of the button.
 * Call doAnimation to perform a simple rotating animation.
 */
//TODO JPP delete ME
class MPActionButton : ConstraintLayout {

    private var filledIcon: Drawable? = null
    private var emptyIcon: Drawable? = null

    private var isAnimating: Boolean = false
    private var isAsClickable: Boolean = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()

//        with(context.obtainStyledAttributes(attrs, R.styleable.MPActionButton)) {
//            filledIcon = getDrawable(R.styleable.MPActionButton_fillIcon)
//            emptyIcon = getDrawable(R.styleable.MPActionButton_emptyIcon)
//            recycle()
//        }

        mpActionButtonImage.setImageDrawable(emptyIcon)
    }

    private fun init() {
        inflate(context, R.layout.layout_mp_action_button, this)
    }

    fun asFilled() {
        mpActionButtonImage.setImageDrawable(filledIcon)
    }

    fun asEmpty() {
        mpActionButtonImage.setImageDrawable(emptyIcon)
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
//        val animation = AnimationUtils.loadAnimation(context, R.anim.action_button_animation)
//        animation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationRepeat(animation: Animation?) {
//                // no-op
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                isAnimating = false
//                syncClickableState()
//            }
//
//            override fun onAnimationStart(animation: Animation?) {
//                isAnimating = true
//                syncClickableState()
//            }
//
//        })
//        startAnimation(animation)
    }

    private fun syncClickableState() {
        isClickable = !isAnimating && isAsClickable
    }
}
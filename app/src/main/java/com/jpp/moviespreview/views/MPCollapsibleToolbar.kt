package com.jpp.moviespreview.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * Custom [MotionLayout] implementation to animate the app bar.
 *
 * - The progress of the transition executed by a [MotionLayout] can be controlled using
 *   [MotionLayout.setProgress].
 * - Changes in the size of the [AppBarLayout] can be detected with a listener
 *   ([AppBarLayout.OnOffsetChangedListener]) and obtain the delta change (as a vertical offset).
 *
 * This custom view uses both capabilities to detect the vertical offset of the [AppBarLayout] change
 * and update the progress of the transition accordingly. That way, we have a custom [MotionLayout]
 * that can change the content with a transition that is based in the interaction of the user
 * with a scrollable view in a [CoordinatorLayout].
 *
 * Source: https://medium.com/google-developers/introduction-to-motionlayout-part-iii-47cd64d51a5
 */
class MPCollapsibleToolbar : MotionLayout, AppBarLayout.OnOffsetChangedListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        progress = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.addOnOffsetChangedListener(this)
    }
}
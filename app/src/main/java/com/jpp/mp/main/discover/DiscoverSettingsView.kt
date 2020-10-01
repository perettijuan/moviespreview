package com.jpp.mp.main.discover

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.mp.R
import com.jpp.mpdesign.anims.MPAnimationAdapter

/**
 * TODO JPP add javadoc
 */
internal class DiscoverSettingsView : ConstraintLayout {

    private var clickableArea: LinearLayout? = null
    private var clickableAreaIcon: ImageView? = null
    private var text1: TextView? = null
    private var text2: TextView? = null
    private var text3: TextView? = null
    private var text4: TextView? = null

    private var targetExpandedHeight = 0
    private var targetCollapsedHeight = 0
    private var isExpanded = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.layout_discover_settings, this)
        clickableArea = findViewById(R.id.clickableArea)
        clickableAreaIcon = findViewById(R.id.clickableAreaIcon)
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        text3 = findViewById(R.id.text3)
        text4 = findViewById(R.id.text4)

        clickableArea?.setOnClickListener {
            updateExpanded()
        }

        viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredHeight != 0) {
                    setCollapsed()
                    viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    private fun setCollapsed() {
        targetExpandedHeight = measuredHeight

        text1?.visibility = View.GONE
        text2?.visibility = View.GONE
        text3?.visibility = View.GONE
        text4?.visibility = View.GONE

        isExpanded = false
    }

    private fun animateToExpanded() {
        targetCollapsedHeight = measuredHeight
        val currentHeight = measuredHeight

        ValueAnimator.ofInt(currentHeight, targetExpandedHeight).apply {
            interpolator = AccelerateInterpolator()
            duration = ANIMATION_DURATION
            addUpdateListener { animation ->
                val calculated = (targetExpandedHeight * animation.animatedFraction).toInt()
                if (calculated >= currentHeight) {
                    layoutParams.height = calculated
                    layoutParams = layoutParams
                }
            }
            addListener(object : MPAnimationAdapter {
                override fun onAnimationEnd(animation: Animator?) {
                    text1?.visibility = View.VISIBLE
                    text2?.visibility = View.VISIBLE
                    text3?.visibility = View.VISIBLE
                    text4?.visibility = View.VISIBLE
                    isExpanded = true
                }
            })
        }.start()

        clickableAreaIcon?.animate()?.rotation(90F)?.setDuration(ANIMATION_DURATION)?.start()

    }

    private fun animateToCollapsed() {
        targetExpandedHeight = measuredHeight
        ValueAnimator.ofInt(measuredHeight, targetCollapsedHeight).apply {
            interpolator = AccelerateInterpolator()
            duration = ANIMATION_DURATION
            addUpdateListener { animation ->
                val calculated =
                    layoutParams.height - (targetCollapsedHeight * animation.animatedFraction).toInt()
                if (calculated >= targetCollapsedHeight) {
                    layoutParams.height = calculated
                    layoutParams = layoutParams
                } else {
                    layoutParams.height = targetCollapsedHeight
                    layoutParams = layoutParams
                }
            }
            addListener(object : MPAnimationAdapter {
                override fun onAnimationStart(animation: Animator?) {
                    text1?.visibility = View.GONE
                    text2?.visibility = View.GONE
                    text3?.visibility = View.GONE
                    text4?.visibility = View.GONE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isExpanded = false
                }
            })
        }.start()

        clickableAreaIcon?.animate()?.rotation(0F)?.setDuration(ANIMATION_DURATION)?.start()
    }

    private fun updateExpanded() {
        if (isExpanded) {
            animateToCollapsed()
        } else {
            animateToExpanded()
        }
    }


    private companion object {
        const val ANIMATION_DURATION = 300L
    }
}
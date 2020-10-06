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
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.main.discover.filters.genres.GenreFilterAdapter
import com.jpp.mp.main.discover.filters.genres.GenreFilterItem
import com.jpp.mpdesign.anims.MPAnimationAdapter

/**
 * TODO JPP add javadoc
 */
internal class DiscoverMoviesSettingsView : ConstraintLayout {

    interface ActionListener {
        fun onExpandCollapseSelected(isExpanded: Boolean)
    }

    private var clickableArea: LinearLayout? = null
    private var clickableAreaIcon: ImageView? = null
    private var genreFilterTitle: TextView? = null
    private var genreFilerList: RecyclerView? = null


    private var targetExpandedHeight = 0
    private var targetCollapsedHeight = 0
    private var isExpanded = false

    var actionListener: ActionListener? = null

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
        genreFilterTitle = findViewById(R.id.genreFilterTitleTextView)
        genreFilerList = findViewById(R.id.genreFilterRecyclerView)

        genreFilerList?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = GenreFilterAdapter()
        }


        clickableArea?.setOnClickListener {
            actionListener?.onExpandCollapseSelected(isExpanded)
        }

        viewTreeObserver?.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredHeight != 0) {
                    initCollapsed()
                    viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    private fun initCollapsed() {
        targetExpandedHeight = measuredHeight

        genreFilterTitle?.visibility = View.GONE
        genreFilerList?.visibility = View.GONE

        isExpanded = false
    }

    fun setExpanded(expanded: Boolean) {
        if (expanded == isExpanded) {
            return
        }

        if (isExpanded) {
            animateToCollapsed(expanded)
        } else {
            animateToExpanded(expanded)
        }
    }

    fun setGenreFilterTitle(@StringRes title: Int) {
        genreFilterTitle?.setText(title)
    }

    fun setGenreList(genreList: List<GenreFilterItem>) {
        (genreFilerList?.adapter as GenreFilterAdapter).submitList(genreList)
    }

    private fun animateToExpanded(finalExpanded: Boolean) {
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
                    genreFilterTitle?.visibility = View.VISIBLE
                    genreFilerList?.visibility = View.VISIBLE
                    isExpanded = finalExpanded
                }
            })
        }.start()

        clickableAreaIcon?.animate()?.rotation(90F)?.setDuration(ANIMATION_DURATION)?.start()

    }

    private fun animateToCollapsed(finalExpanded: Boolean) {
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
                    genreFilterTitle?.visibility = View.GONE
                    genreFilerList?.visibility = View.GONE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isExpanded = finalExpanded
                }
            })
        }.start()

        clickableAreaIcon?.animate()?.rotation(0F)?.setDuration(ANIMATION_DURATION)?.start()
    }

    private companion object {
        const val ANIMATION_DURATION = 300L
    }
}

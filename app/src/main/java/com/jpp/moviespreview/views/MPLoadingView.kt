package com.jpp.moviespreview.views


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.getColor

/**
 * Custom view to show the loading state in the animation. It shows an infinite progress bar
 * with two colors.
 */
class MPLoadingView : View {

    private val animDuration = 750

    private var primaryColor: Paint? = null
    private var secondaryColor: Paint? = null

    private var sweepAngle: Int = 0
    private var startAngle: Int = 0

    private var strokeSize: Int = 0

    private lateinit var viewBounds: RectF
    private var currentColor: Paint? = null

    private var sweepAnim: ValueAnimator? = null
    private var startAnim: ValueAnimator? = null
    private var finalAnim: ValueAnimator? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    @SuppressLint("Recycle")
    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        context.obtainStyledAttributes(attrs, R.styleable.MPLoadingView, defStyleAttr, 0)
                .apply {
                    strokeSize = getDimensionPixelSize(R.styleable.MPLoadingView_stroke, resources.getDimensionPixelSize(R.dimen.mp_loading_view_stroke_width))
                    primaryColor = createPaint(Paint.Style.STROKE, strokeSize, loadColor(this, R.styleable.MPLoadingView_primaryColor, R.color.primaryColor))
                    secondaryColor = createPaint(Paint.Style.STROKE, strokeSize, loadColor(this, R.styleable.MPLoadingView_secondaryColor, R.color.accentColor))
                }.run {
                    recycle()
                }
    }

    /**
     * Load the color from xml attributes or fallback to the default if not found
     *
     * @param a            attributes typed array
     * @param index        the styleable index
     * @param defaultColor the default color resource personId
     * @return the color to use
     */
    @ColorInt
    private fun loadColor(a: TypedArray, @StyleableRes index: Int, @ColorRes defaultColor: Int): Int {
        var loadedColor = getColor(defaultColor)
        val colorList = a.getColorStateList(index)

        if (colorList != null) {
            loadedColor = colorList.defaultColor
        }

        return loadedColor
    }

    /**
     * Configure the animations that interpolate the arc values to achieve the loading effect
     */
    private fun setupAnimations() {
        // - Head and tail start together, when the head finishes the full spin the tail catches up - //
        sweepAnim = createAnimator(0, FULL_CIRCLE, animDuration).apply {
            addUpdateListener { animation -> sweepAngle = animation.animatedValue as Int }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    finalAnim?.start()
                }
            })
        }

        startAnim = createAnimator(0, QUARTER_CIRCLE, animDuration).apply {
            addUpdateListener { animation ->
                startAngle = animation.animatedValue as Int
                invalidate()
            }
        }

        finalAnim = createAnimator(QUARTER_CIRCLE, FULL_CIRCLE, animDuration).apply {
            addUpdateListener { animation ->
                startAngle = animation.animatedValue as Int
                invalidate()
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    updateColor()
                    sweepAnim?.start()
                    startAnim?.start()
                }
            })
        }

        sweepAnim?.start()
        startAnim?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    /**
     * Update the color to use in this round.
     * The color changes each round between the primary and secondary color
     */
    private fun updateColor() {
        currentColor = if (currentColor === primaryColor) secondaryColor else primaryColor
    }

    /**
     * Create an animator that will interpolate the angles of the circle
     *
     * @param startAngle the start value of the angle in degrees. Eg: 0
     * @param endAngle   the end value of the angle in degrees. Eg: 270
     * @param animDuration   the duration of the animation
     * @return an animator that will interpolate the angles between startAngle and endAngle
     */
    private fun createAnimator(startAngle: Int, endAngle: Int, animDuration: Int) =
        ValueAnimator.ofInt(startAngle, endAngle).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = animDuration.toLong()
        }

    /**
     * Create the paint that will be used to draw the view
     *
     * @param pStyle       the paint style
     * @param pStrokeWidth the stroke width
     * @param hex         the color to paint
     * @return the paint to apply
     */
    private fun createPaint(pStyle: Paint.Style, pStrokeWidth: Int, @ColorInt hex: Int) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = pStyle
            strokeWidth = pStrokeWidth.toFloat()
            color = hex
        }

    /**
     * When the view size changes, calculate its new size and start rotating from the center
     * {@inheritDoc}
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        viewBounds = RectF(strokeSize.toFloat(), strokeSize.toFloat(), (width - strokeSize).toFloat(), (height - strokeSize).toFloat())

        if (this.animation != null) {
            this.animation.cancel()
        }

        startAnimation(RotateAnimation(0f, FULL_CIRCLE.toFloat(), viewBounds.centerX(), viewBounds.centerY()).apply {
            duration = 2000
            repeatCount = -1
            repeatMode = Animation.RESTART
            interpolator = LinearInterpolator()
        })
    }

    /**
     * Draw the arc of the loading progress
     *
     *
     * {@inheritDoc}
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        if (visibility == VISIBLE) {
            currentColor?.run {
                canvas.drawArc(viewBounds, startAngle.toFloat(), (sweepAngle - startAngle).toFloat(), false, this)
            }
        }
    }

    /**
     * Call this on activity start to begin animations
     */
    fun start() {
        setupAnimations()
    }

    /**
     * Call this on activity stop to finish animations
     */
    fun stop() {
        cleanAnimator(startAnim)
        cleanAnimator(sweepAnim)
        cleanAnimator(finalAnim)
    }

    /**
     * Clean the animators so as not to leak memory
     *
     * @param animator the animator to clean
     */
    private fun cleanAnimator(animator: ValueAnimator?) {
        if (animator != null) {
            animator.cancel()
            animator.removeAllListeners()
            animator.removeAllUpdateListeners()
        }
    }

    companion object {
        const val FULL_CIRCLE = 360
        const val QUARTER_CIRCLE = 90
    }
}
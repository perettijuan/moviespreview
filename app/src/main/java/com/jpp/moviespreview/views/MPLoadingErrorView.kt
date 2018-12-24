package com.jpp.moviespreview.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.*
import kotlinx.android.synthetic.main.loading_error_layout.view.*

class MPLoadingErrorView : ConstraintLayout {


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.loading_error_layout, this)
    }


    /**
     * Shows the loading spinner immediately (no animation performed).
     */
    fun showLoadingImmediate() {
        loadingView.let {
            it.start()
            it.toOneAlpha()
        }
        errorImageView.toZeroAlpha()
        errorTitleTextView.toZeroAlpha()
        errorActionButton.toZeroAlpha()
    }

    /**
     * Hides the loading spinner (stopping the animation)
     * and the rest of the views immediately.
     */
    fun hideImmediate() {
        loadingView.let {
            it.toZeroAlpha()
            it.stop()
        }
        errorImageView.toZeroAlpha()
        errorTitleTextView.toZeroAlpha()
        errorActionButton.toZeroAlpha()
    }

    /**
     * Shows the loading spinner performing a fade in animation.
     */
    fun animateToLoading(startDelay: Long = 100,
                         duration: Long = 200,
                         callback: (() -> Unit)? = null) {
        errorImageView.animateToZeroAlpha(startDelay, duration)
        errorTitleTextView.animateToZeroAlpha(startDelay, duration)
        errorActionButton.animateToZeroAlpha(startDelay, duration)
        loadingView.animateToOneAlpha(startDelay, duration) {
            loadingView.start()
            callback?.invoke()
        }
    }

    /**
     * Hides the view performing a fade out animation.
     */
    fun hideWithAnimation(startDelay: Long = 100,
                          duration: Long = 200,
                          callback: () -> Unit) {
        animate()
                .alpha(0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        hideImmediate()
                        callback.invoke()
                    }
                })
                .setStartDelay(startDelay)
                .setDuration(duration)
                .start()
    }

    /**
     * Animates the view to hide the loading state and show the no connectivity error state.
     */
    fun animateToNoConnectivityError(startDelay: Long = 100,
                                     duration: Long = 200,
                                     retryAction: () -> Unit) {
        animateToError(R.drawable.ic_cloud_off,
                R.string.error_no_network_connection_message,
                R.string.error_retry,
                startDelay,
                duration,
                retryAction)

    }

    /**
     * Animates the view to hide the loading state and show the unknown error state.
     */
    fun animateToUnknownError(startDelay: Long = 100,
                              duration: Long = 200,
                              retryAction: () -> Unit) {
        animateToError(R.drawable.ic_alert_circle,
                R.string.error_unexpected_error_message,
                R.string.error_retry,
                startDelay,
                duration,
                retryAction)
    }

    private fun animateToError(@DrawableRes errorImageIcon: Int,
                               @StringRes errorTitle: Int,
                               @StringRes errorButton: Int,
                               startDelay: Long,
                               duration: Long,
                               retryAction: () -> Unit) {
        errorImageView.setImageResource(errorImageIcon)
        errorTitleTextView.apply { text = getStringFromResources(errorTitle) }
        errorActionButton.apply {
            text = getStringFromResources(errorButton)
            setOnClickListener { retryAction.invoke() }
        }
        errorImageView.animateToOneAlpha(startDelay, duration)
        errorTitleTextView.animateToOneAlpha(startDelay, duration)
        errorActionButton.animateToOneAlpha(startDelay, duration)
        loadingView.animateToZeroAlpha(startDelay, duration) {
            loadingView.stop()
        }
    }
}
package com.jpp.mpdesign.anims

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.databinding.BindingAdapter
import com.jpp.mpdesign.R

/**
 * Contains all binding adapters used with Android Data Binding library.
 */
object BindingAdapters {

    /**
     * Custom Binding Adapter used to animate the visibility of a view from
     * a given value to [visibility]. If the View's original visibility was
     * INVISIBLE, the adapter will animate to VISIBLE - using the alpha property.
     * If the View's original visibility was VISIBLE, it will animate to
     * INVISIBLE.
     *
     * To use this adapter, use {app:animatedVisibility}.
     *
     * Source: https://medium.com/androiddevelopers/android-data-binding-animations-55f6b5956a64
     */
    @JvmStatic
    @BindingAdapter("animatedVisibility")
    fun setVisibility(view: View, visibility: Int) {
        view.visibility = View.INVISIBLE

        // Were we animating before? If so, what was the visibility?
        val endAnimVisibility = view.getTag(R.id.finalVisibility) as Int?
        val oldVisibility = endAnimVisibility ?: view.visibility

        if (oldVisibility == visibility) {
            // just let it finish any current animation.
            return
        }

        val isVisible = oldVisibility == View.VISIBLE
        val willBeVisible = visibility == View.VISIBLE

        var startAlpha = if (isVisible) 1f else 0f
        if (endAnimVisibility != null) {
            startAlpha = view.alpha
        }
        val endAlpha = if (willBeVisible) 1f else 0f
        view.visibility = View.VISIBLE

        // Now create an animator
        ObjectAnimator.ofFloat(view, View.ALPHA, startAlpha, endAlpha)
                .apply {
                    setAutoCancel(true)
                    duration = 600

                    addListener(object : AnimatorListenerAdapter() {
                        private var isCanceled: Boolean = false

                        override fun onAnimationStart(anim: Animator) {
                            view.setTag(R.id.finalVisibility, visibility)
                        }

                        override fun onAnimationCancel(anim: Animator) {
                            isCanceled = true
                        }

                        override fun onAnimationEnd(anim: Animator) {
                            view.setTag(R.id.finalVisibility, null)
                            if (!isCanceled) {
                                view.alpha = 1f
                                view.visibility = visibility
                            }
                        }
                    })
                }.start()
    }
}

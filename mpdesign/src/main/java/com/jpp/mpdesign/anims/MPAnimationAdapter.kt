package com.jpp.mpdesign.anims

import android.animation.Animator

/**
 * [Animator.AnimatorListener] implementation to avoid overriding all methods
 * every time the interface is needed.
 */
interface MPAnimationAdapter : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) = Unit
    override fun onAnimationEnd(animation: Animator?) = Unit
    override fun onAnimationCancel(animation: Animator?) = Unit
    override fun onAnimationRepeat(animation: Animator?) = Unit
}
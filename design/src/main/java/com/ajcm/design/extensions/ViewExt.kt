package com.ajcm.design.extensions

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView

fun LottieAnimationView.addOnFinishListener(completion: () -> Unit) {
    this.addAnimatorListener(object : Animator.AnimatorListener {

        override fun onAnimationStart(p0: Animator?) = Unit
        override fun onAnimationCancel(p0: Animator?) = Unit
        override fun onAnimationRepeat(p0: Animator?) = Unit

        override fun onAnimationEnd(p0: Animator?) {
            completion()
        }
    })
}

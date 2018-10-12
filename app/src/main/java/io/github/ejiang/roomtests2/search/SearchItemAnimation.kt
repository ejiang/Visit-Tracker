package io.github.ejiang.roomtests2.search

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

class SearchItemAnimation {
    companion object {
        val DURATION : Long = 150

        fun animateLeftRight(v: View, position: Int) {
            val notFirst = position == 1
            v.translationX = -400f
            v.alpha = 0f
            val animatorSet = AnimatorSet()
            val animatorX : ObjectAnimator = ObjectAnimator.ofFloat(v, "translationX", -400f, 0f)
            val animatorAlpha : ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f)
            ObjectAnimator.ofFloat(v, "alpha", 0f).start()
            animatorX.startDelay = if (notFirst) DURATION else ((position + 1) * DURATION)
            animatorX.duration = (if (notFirst) 2 else 1) * DURATION
            animatorSet.playTogether(animatorX, animatorAlpha)
            animatorSet.start()
        }
    }
}
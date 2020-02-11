package cn.ifafu.ifafu.util

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup


object AnimationUtils {
    /**
     * 将view从不可见变为可见的动画，原理:动态改变其LayoutParams.height的值
     * @param view 要展示动画的view
     */
    fun visibleAnimator(view: View) {
        var viewHeight: Int = view.height
        if (viewHeight == 0) {
            val width: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val height: Int = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(width, height)
            viewHeight = view.measuredHeight
        }
        val animator: ValueAnimator = ValueAnimator.ofInt(0, viewHeight)
        animator.addUpdateListener { animation ->
            val params: ViewGroup.LayoutParams = view.layoutParams
            params.height = animation.animatedValue as Int
            view.layoutParams = params
        }
        animator.start()
    }
}
package io.github.ejiang.roomtests2.search

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import io.github.ejiang.roomtests2.R
import kotlin.math.abs

class PullLayout : ViewGroup {

    companion object {
        private val DRAG_MAX_DISTANCE = 64
        private val INVALID_POINTER = -1
        private val DRAG_RATE = 0.5f
    }

    lateinit var releaseListener : PullReleaseListener

    private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)
    val parentOffsetInWindow = IntArray(2)
    var maxDragDistance = -1f
    var currentOffsetTop : Int = 0

    var activePointerId : Int = 0
    var lastDownY: Float = 0.toFloat()

    var totalUnconsumed : Float = 0f
    var nestedScrollInProgress = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        maxDragDistance = DRAG_MAX_DISTANCE * resources.displayMetrics.density
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val v = getChildAt(0)
        v?.layout(l,t,r,b)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val v = getChildAt(0)
        v?.measure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        nestedScrollInProgress = true
        totalUnconsumed = 0f
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy < 0 && totalUnconsumed > 0) {
            if (abs(dy) > totalUnconsumed) {
                consumed[1] = dy + totalUnconsumed.toInt()
                totalUnconsumed = 0f
            } else {
                totalUnconsumed += dy.toFloat()
                consumed[1] = dy
            }
            onDrag(totalUnconsumed)
        }
        dispatchNestedPreScroll(dx, dy, consumed, null)
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(child: View) {
        nestedScrollInProgress = false
        nestedScrollingParentHelper.onStopNestedScroll(child)

        if (totalUnconsumed > 0) {
            onRelease()
            totalUnconsumed = 0f
        }

        stopNestedScroll()
    }

    override fun onNestedScroll(target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow)

        val dy = dyUnconsumed + parentOffsetInWindow[1]
        if (dy > 0) {
            totalUnconsumed += dy.toFloat()
            onDrag(totalUnconsumed)
        }
    }

    override fun onNestedPreFling(target: View?, velocityX: Float, velocityY: Float): Boolean {
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun onNestedFling(target: View?, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    private fun onRelease() {
        val v = getChildAt(0)
        toStartAnimation.apply {
            reset()
            duration = 200L
            interpolator = DecelerateInterpolator(2f)
        }
        v.clearAnimation()
        v.startAnimation(toStartAnimation)

        // then, also, add to the RV, if certain conditions met
        // call the release listener
        if (currentOffsetTop == calculateTargetOffset(1000f))
            releaseListener.onRelease()
    }

    private val toStartAnimation = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val offset = calculateDistanceToStartPosition(interpolatedTime)
            offsetTarget(offset)
        }
    }

    private fun calculateDistanceToStartPosition(interpolatedTime: Float): Int {
        val targetTop = currentOffsetTop - (currentOffsetTop * interpolatedTime)
        val roundedTop = Math.floor(targetTop.toDouble()).toInt()
        val target = getChildAt(0)
        return roundedTop + (target?.top ?: 0)
    }

    private fun onDrag(overscroll: Float) {
        val targetOffset = calculateTargetOffset(overscroll)
        // 1000f is some kind of ridiculous overscroll, used to calculate the max distance
        if (currentOffsetTop == calculateTargetOffset(1000f)) {
            // we are at the end
            setBackgroundColor(Color.parseColor("#93DFCB"))
        } else {
            setBackgroundColor(ContextCompat.getColor(context, R.color.grey_10))
        }
        offsetTarget(targetOffset - currentOffsetTop)
    }

    private fun calculateTargetOffset(overscroll: Float): Int {
        val originalDragPercent = overscroll / maxDragDistance
        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        val extraOS = Math.abs(overscroll) - maxDragDistance
        val slingshotDist = maxDragDistance
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow(
                (tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f
        return (slingshotDist * dragPercent + extraMove).toInt()
    }

    private fun offsetTarget(offset: Int) {
        val v = getChildAt(0)
        v.offsetTopAndBottom(-offset)
        currentOffsetTop = -(v?.top ?: 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked

        val downY = getMotionEventY(event, activePointerId)
        Log.d("TAG", "${downY}")

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val overscroll = calculateOverscroll(event)
                onDrag(overscroll)
                return true
            }
        }
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    private fun calculateOverscroll(event: MotionEvent): Float {
        val y = getMotionEventY(event, activePointerId)
        if (y == -1f) {
            return -1f
        }
        return (y - lastDownY) * DRAG_RATE
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = ev.findPointerIndex(activePointerId)
        if (index < 0) {
            return -1f
        }
        return ev.getY(index)
    }
}

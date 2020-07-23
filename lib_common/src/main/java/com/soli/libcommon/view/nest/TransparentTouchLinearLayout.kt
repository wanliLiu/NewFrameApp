package com.soli.libcommon.view.nest

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent3
import com.soli.libcommon.view.nest.DealTransparentTouchEvent
import com.soli.libcommon.view.nest.TransparentTouchEvent

/**
 * 专门用来分发事件用的,走事件流程
 *
 * @author Soli
 * @Time 2020/7/22 11:07
 */
class TransparentTouchLinearLayout(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3,
    TransparentTouchEvent {

    constructor(ctx: Context) : this(ctx, null, 0)
    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    private val dealTool: DealTransparentTouchEvent =
        DealTransparentTouchEvent(context, this)


    /**------------------------------------------需要添加的部分--------------------------**/
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        dealTool.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dealTool.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return dealTool.onTouchEvent(ev)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        dealTool.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
        dealTool.onStartNestedScroll(child, target, axes, type)

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        dealTool.onNestedPreScroll(target, dx, dy, consumed, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        dealTool.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        dealTool.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        dealTool.onStopNestedScroll(target, type)
    }


    //ViewParent
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return dealTool.onStartNestedScroll(child, target, nestedScrollAxes)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        dealTool.onNestedScrollAccepted(child, target, axes)
    }

    override fun onStopNestedScroll(child: View) {
        dealTool.onStopNestedScroll(child)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        dealTool.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        dealTool.onNestedPreScroll(target, dx, dy, consumed)
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return dealTool.onNestedFling(target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dealTool.onNestedPreFling(target, velocityX, velocityY)
    }


    override fun getNestedScrollAxes(): Int {
        return dealTool.nestedScrollAxes
    }
    /**------------------------------------------需要添加的部分--------------------------**/

}
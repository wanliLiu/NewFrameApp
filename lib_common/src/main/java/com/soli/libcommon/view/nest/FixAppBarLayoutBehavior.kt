package com.soli.libcommon.view.nest

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout


/*
 * 处理惯性滑动  出现的一些问题
 * @author soli
 * @Time 2018/12/20 21:59
 */
open class FixAppBarLayoutBehavior : AppBarLayout.Behavior {

    constructor() : super()

    constructor(context: Context? = null, attrs: AttributeSet? = null) : super(context, attrs)

    private var isFlinging = false
    private var shouldBlockNestedScroll = false

    private val filedScroller by lazy {
        //父类AppBarLayout.Behavior  父类的父类   HeaderBehavior
        val reflex_class = javaClass.superclass?.superclass?.superclass
        val fieldScroller = reflex_class?.getDeclaredField("scroller")
        if (fieldScroller != null) {
            fieldScroller.isAccessible = true
        }
        fieldScroller
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        shouldBlockNestedScroll = false
        if (isFlinging) {
            shouldBlockNestedScroll = true
        }
        if (ev.action == MotionEvent.ACTION_DOWN)
            stopIfFiling()  //手指触摸屏幕的时候停止fling事件

        return super.onInterceptTouchEvent(parent, child, ev)
    }

    /**
     * 反射获得滑动属性。
     *
     * @param context
     */
    private fun stopIfFiling() {
        try {
            val scroller = filedScroller?.get(this)
            if (scroller != null && scroller is OverScroller) {
                if (!scroller.isFinished)
                    scroller.abortAnimation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        stopIfFiling()
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        //type返回1时，表示当前target处于非touch的滑动，
        //该bug的引起是因为appbar在滑动时，CoordinatorLayout内的实现NestedScrollingChild2接口的滑动子类还未结束其自身的fling
        //所以这里监听子类的非touch时的滑动，然后block掉滑动事件传递给AppBarLayout
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            isFlinging = true
        }
        if (!shouldBlockNestedScroll) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        if (!shouldBlockNestedScroll) {
            super.onNestedScroll(
                coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type
            )
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
        isFlinging = false
        shouldBlockNestedScroll = false
    }
}

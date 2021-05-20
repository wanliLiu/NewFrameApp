package com.soli.newframeapp.drag

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.max
import kotlin.math.min

/**
 *
 *
 *
 * Created by sofia on 2021/5/20.
 */
class DragView : FrameLayout {

    private var dragView: View? = null
    private val dragHelper: ViewDragHelper
    private var defaultLeft = 0
    private var defaultTop = 0

    companion object {
        private val TAG = DragView::class.java.simpleName
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        dragHelper = ViewDragHelper.create(this, 1.0f, dragCallback())
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            dragView = getChildAt(0)
            dragView?.viewTreeObserver?.addOnGlobalLayoutListener {
                defaultLeft = dragView!!.left
                defaultTop = dragView!!.top
                Log.e(TAG, "default Position : X = $defaultLeft;Y = $defaultTop")
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            dragHelper.cancel()
            return false
        }
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    /**
     *
     */
    private inner class dragCallback : ViewDragHelper.Callback() {

        private var changeLeft = 0
        private var changeTop = 0

        private val finalLeft: Int
            get() = when {
                changeLeft > measuredWidth / 2 -  dragView!!.measuredWidth / 2 -> measuredWidth - dragView!!.measuredWidth
                else -> 0
            }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === dragView
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            Log.e(TAG, "clampViewPositionHorizontal $left,$dx")
            val leftbound = paddingLeft
            val rightbound = measuredWidth - paddingRight - child.measuredWidth
            return min(max(leftbound, left), rightbound)
            //            return left;
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            Log.e(TAG, "clampViewPositionVertical $top,$dy")
            val topbound = paddingTop
            val bottombound = measuredHeight - paddingBottom - child.measuredHeight
            return min(max(top, topbound), bottombound)
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            Log.e(TAG, "onViewPositionChanged $left,$top,$dx,$dy")
            changeLeft = left
            changeTop = top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.e(TAG, "onViewReleased $xvel,$yvel")
//            dragHelper.smoothSlideViewTo(releasedChild, initLeft, initRight);
            dragHelper.settleCapturedViewAt(finalLeft, changeTop)
//            dragHelper.settleCapturedViewAt(defaultLeft, defaultTop)
            invalidate()
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return measuredWidth - child.measuredWidth
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return measuredHeight - child.measuredHeight
        }

        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            super.onEdgeDragStarted(edgeFlags, pointerId)
            if (edgeFlags and ViewDragHelper.EDGE_RIGHT != 0) {
                dragHelper.captureChildView(dragView!!, pointerId)
            }
        }
    }
}
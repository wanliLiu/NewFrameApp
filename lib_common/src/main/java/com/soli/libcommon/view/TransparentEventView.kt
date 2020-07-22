package com.soli.libcommon.view

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.*
import kotlin.math.abs

/**
 * 专门用来分发事件用的,走事件流程
 *
 * @author Soli
 * @Time 2020/7/22 11:07
 */
class TransparentEventView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent3, NestedScrollingChild3 {

    constructor(ctx: Context) : this(ctx, null, 0)
    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    /**
     * Position of the last motion event.
     */
    private var mLastMotionY = 0

    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private var mIsBeingDragged = false

    /**
     * Determines speed during touch scrolling
     */
    private var mVelocityTracker: VelocityTracker? = null
    private var mTouchSlop = 0
    private var mMinimumVelocity = 0
    private var mMaximumVelocity = 0

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private var mActivePointerId = INVALID_POINTER

    /**
     * Used during scrolling to retrieve the new offset within the window.
     */
    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)
    private var mNestedYOffset = 0

    private val mParentHelper: NestedScrollingParentHelper
    private val mChildHelper: NestedScrollingChildHelper

    init {

        requestDisallowParentInterceptEvent(true)

        isFocusable = true
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        setWillNotDraw(false)

        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity

        mParentHelper = NestedScrollingParentHelper(this)
        mChildHelper = NestedScrollingChildHelper(this)
        // ...because why else would you be using this widget?
        isNestedScrollingEnabled = true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        requestDisallowParentInterceptEvent(true)
        return super.dispatchTouchEvent(ev)
    }

    // NestedScrollingChild3
    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray
    ) {
        mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type,
            consumed
        )
    }

    // NestedScrollingChild2
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        mChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    // NestedScrollingChild
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH)
    }

    override fun stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
    }

    override fun hasNestedScrollingParent(): Boolean {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    // NestedScrollingParent3
    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray
    ) {
        mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            null,
            type,
            consumed
        )
    }

    // NestedScrollingParent2
    override fun onStartNestedScroll(
        child: View, target: View, axes: Int,
        type: Int
    ): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(
        child: View, target: View, axes: Int,
        type: Int
    ) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, type: Int
    ) {
        mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            null,
            type,
            null
        )
    }

    override fun onNestedPreScroll(
        target: View, dx: Int, dy: Int, consumed: IntArray,
        type: Int
    ) {
        dispatchNestedPreScroll(dx, dy, consumed, null, type)
    }

    // NestedScrollingParent
    override fun onStartNestedScroll(
        child: View, target: View, nestedScrollAxes: Int
    ): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(
        child: View, target: View, nestedScrollAxes: Int
    ) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int
    ) {
        mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            null,
            ViewCompat.TYPE_TOUCH,
            null
        )
    }

    override fun onNestedPreScroll(
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray
    ) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return if (!consumed) {
            dispatchNestedFling(0f, velocityY, true)
        } else false
    }

    override fun onNestedPreFling(
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }


    private fun initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker!!.clear()
        }
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun inChild(x: Int, y: Int): Boolean {
        if (childCount > 0) {
            val scrollY = scrollY
            val child = getChildAt(0)
            return !(y < child.top - scrollY || y >= child.bottom - scrollY || x < child.left || x >= child.right)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true
        }
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {

                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionY is set to the y value
                 * of the down event.
                 */
                val activePointerId = mActivePointerId
                if (activePointerId != INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    val pointerIndex = ev.findPointerIndex(activePointerId)
                    if (pointerIndex != -1) {
                        val y = ev.getY(pointerIndex).toInt()
                        val yDiff = abs(y - mLastMotionY)
                        if (yDiff > mTouchSlop
                            && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL == 0
                        ) {
                            mIsBeingDragged = true
                            mLastMotionY = y
                            initVelocityTrackerIfNotExists()
                            mVelocityTracker!!.addMovement(ev)
                            mNestedYOffset = 0
                        }
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                val y = ev.y.toInt()
                if (!inChild(ev.x.toInt(), y)) {
                    mIsBeingDragged = false
                    recycleVelocityTracker()
                } else {
                    /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                    mLastMotionY = y
                    mActivePointerId = ev.getPointerId(0)
                    initOrResetVelocityTracker()
                    mVelocityTracker!!.addMovement(ev)
                    /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't. mScroller.isFinished should be false when
                 * being flinged. We need to call computeScrollOffset() first so that
                 * isFinished() is correct.
                 */mIsBeingDragged = false
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                /* Release the drag */
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
                recycleVelocityTracker()
                stopNestedScroll(ViewCompat.TYPE_TOUCH)
            }
            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        initVelocityTrackerIfNotExists()
        val actionMasked = ev.actionMasked
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0
        }
        val vtev = MotionEvent.obtain(ev)
        vtev.offsetLocation(0f, mNestedYOffset.toFloat())
        when (actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (childCount == 0) {
                    return false
                }

                // Remember where the motion event started
                mLastMotionY = ev.y.toInt()
                mActivePointerId = ev.getPointerId(0)
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
            }
            MotionEvent.ACTION_MOVE -> {
                requestDisallowParentInterceptEvent(true)

                val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                if (activePointerIndex != -1) {
                    val y = ev.getY(activePointerIndex).toInt()
                    var deltaY = mLastMotionY - y
                    if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                        mIsBeingDragged = true
                        if (deltaY > 0) {
                            deltaY -= mTouchSlop
                        } else {
                            deltaY += mTouchSlop
                        }
                    }
                    if (mIsBeingDragged) {
                        // Start with nested pre scrolling
                        if (dispatchNestedPreScroll(
                                0, deltaY, mScrollConsumed, mScrollOffset,
                                ViewCompat.TYPE_TOUCH
                            )
                        ) {
                            deltaY -= mScrollConsumed[1]
                            mNestedYOffset += mScrollOffset[1]
                        }

                        // Scroll to follow the motion event
                        mLastMotionY = y - mScrollOffset[1]
                        val oldY = scrollY
                        val scrolledDeltaY = scrollY - oldY
                        val unconsumedY = deltaY - scrolledDeltaY
                        mScrollConsumed[1] = 0
                        dispatchNestedScroll(
                            0, scrolledDeltaY, 0, unconsumedY, mScrollOffset,
                            ViewCompat.TYPE_TOUCH, mScrollConsumed
                        )
                        mLastMotionY -= mScrollOffset[1]
                        mNestedYOffset += mScrollOffset[1]
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                requestDisallowParentInterceptEvent(false)
                val velocityTracker = mVelocityTracker
                velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val initialVelocity = velocityTracker.getYVelocity(mActivePointerId).toInt()
                if (abs(initialVelocity) >= mMinimumVelocity) {
                    if (!dispatchNestedPreFling(0f, -initialVelocity.toFloat())) {
                        dispatchNestedFling(0f, -initialVelocity.toFloat(), true)
                    }
                }
                mActivePointerId = INVALID_POINTER
                endDrag()
            }
            MotionEvent.ACTION_CANCEL -> {
                requestDisallowParentInterceptEvent(false)
                mActivePointerId = INVALID_POINTER
                endDrag()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                mLastMotionY = ev.getY(index).toInt()
                mActivePointerId = ev.getPointerId(index)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                requestDisallowParentInterceptEvent(false)
                onSecondaryPointerUp(ev)
                mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId)).toInt()
            }
        }
        if (mVelocityTracker != null) {
            mVelocityTracker!!.addMovement(vtev)
        }
        vtev.recycle()
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionY = ev.getY(newPointerIndex).toInt()
            mActivePointerId = ev.getPointerId(newPointerIndex)
            if (mVelocityTracker != null) {
                mVelocityTracker!!.clear()
            }
        }
    }

    /**
     * @param event
     */
    private fun requestDisallowParentInterceptEvent(event: Boolean) {
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(event)
    }

    private fun endDrag() {
        mIsBeingDragged = false
        recycleVelocityTracker()
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
    }

    companion object {
        private const val TAG = "NestedScrollView"

        /**
         * Sentinel value for no current active pointer.
         * Used by [.mActivePointerId].
         */
        private const val INVALID_POINTER = -1
    }

}
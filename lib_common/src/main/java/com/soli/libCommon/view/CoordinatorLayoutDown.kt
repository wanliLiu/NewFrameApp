package com.soli.libCommon.view

import android.animation.ValueAnimator
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.math.MathUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.soli.libCommon.R

/**
 * Created by soli on 18-3-9.
 */
open class CoordinatorLayoutDown constructor(ctx: Context, attrs: AttributeSet) : CoordinatorLayout(ctx, attrs) {

    //appbarLayout 的offset为0的时候可以操作
    var canDetect: Boolean = false

    val mTouchSlop by lazy {
        ViewConfiguration.get(context).scaledTouchSlop
    }
    var mLastMotionY: Float = 0.0f

    var mIsBeingDragged: Boolean = false

    var dragDistance: Float = 0.0f

    val topImage: View by lazy { this.findViewById<View>(R.id.zoom_image) }
    val topImageMinHeight by lazy { topImage.layoutParams.height }
    /**
     *
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (canDetect) {
            val action = ev.action

            if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged)
                return true

            val y = ev.y
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    mIsBeingDragged = false
                    mLastMotionY = y
                    dragDistance = 0.0f
                }
                MotionEvent.ACTION_MOVE -> {
                    val ydiff = y - mLastMotionY
                    mIsBeingDragged = ydiff > 0 //&& ydiff >= mTouchSlop
                    dragDistance = ydiff
                }
                MotionEvent.ACTION_CANCEL -> mIsBeingDragged = false
            }

            return if (mIsBeingDragged) true else super.onInterceptTouchEvent(ev)
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     *
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (canDetect) {
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastMotionY = y
                }
                MotionEvent.ACTION_MOVE -> {
                    val ydiff = y - mLastMotionY
                    dragDistance += ydiff
                    Log.e("distance", dragDistance.toString())
                    Log.e("ydiff", ydiff.toString())
                    changeTopImageHeight(ydiff.toInt())
                    mLastMotionY = y
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> releaseToInitPosition()
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun changeTopImageHeight(diff: Int) {
        topImage.layoutParams.height =
                MathUtils.clamp(topImage.layoutParams.height + diff, topImageMinHeight, topImageMinHeight * 2)
        topImage.requestLayout()
    }

    /**
     *
     */
    private fun releaseToInitPosition() {
        val height = topImage.layoutParams.height
        if (height > topImageMinHeight) {
            val animation = ValueAnimator.ofInt(height, topImageMinHeight)
//            animation.interpolator = AccelerateInterpolator()
            animation.addUpdateListener { animation ->
                run {
                    topImage.layoutParams.height = animation.animatedValue as Int
                    topImage.requestLayout()
                }
            }
            animation.start()
        }
    }
}
package com.soli.libCommon.view

import android.animation.ValueAnimator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.soli.libCommon.R
import com.soli.libCommon.base.Constant
import com.soli.libCommon.util.MLog

/*
 * @author soli
 * @Time 2019/1/13 22:15
 */
class AppbarZoomBehavior : AppBarLayout.Behavior {

    private var mImageView: View? = null
    private var mAppbarHeight = 0//记录AppbarLayout原始高度
    private var mImageViewHeight = 0//记录ImageView原始高度
    private var mTotalDy = 0f//手指在Y轴滑动的总距离
    private var mScaleValue = 0f//图片缩放比例
    private var mLastBottom = 0//Appbar的变化高度

    private val MAX_ZOOM_HEIGHT = 800f//放大最大高度

    private var isAnimate: Boolean = false//是否做动画标志

    private var valueAnimator: ValueAnimator? = null

    private var canDetect: Boolean = false

    private val mTouchSlop by lazy {
        ViewConfiguration.get(Constant.getContext()).scaledTouchSlop
    }
    private var mLastMotionY: Float = 0.0f

    private var mIsBeingDragged: Boolean = false

    private var dragDistance: Float = 0.0f

    constructor()


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        MLog.e("event","onInterceptTouchEvent")
//        if (child.bottom >= mAppbarHeight) {
//            val action = ev.action
//
//            if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged)
//                return true
//
//            val y = ev.y
//            when (ev.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    mIsBeingDragged = false
//                    mLastMotionY = y
//                    dragDistance = 0.0f
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val ydiff = y - mLastMotionY
//                    mIsBeingDragged = ydiff > 0 //&& ydiff >= mTouchSlop
//                    dragDistance = ydiff
//                }
//                MotionEvent.ACTION_CANCEL -> mIsBeingDragged = false
//            }
//
//            return true
//        }

        return super.onInterceptTouchEvent(parent, child, ev)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: AppBarLayout, ev: MotionEvent): Boolean {
        MLog.e("event","onTouchEvent")
//        if (child.bottom >= mAppbarHeight) {
//            val y = ev.y
//            when (ev.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    mLastMotionY = y
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val ydiff = y - mLastMotionY
//                    dragDistance += ydiff
//                    Log.e("distance", dragDistance.toString())
//                    Log.e("ydiff", ydiff.toString())
//                    zoomHeaderImageView(child, ydiff.toInt())
//                    mLastMotionY = y
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> recovery(child)
//            }
//            return true
//        }
        return super.onTouchEvent(parent, child, ev)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, abl: AppBarLayout, layoutDirection: Int): Boolean {
        val handled = super.onLayoutChild(parent, abl, layoutDirection)
        init(abl)
        return handled
    }

    /**
     * 进行初始化操作，在这里获取到ImageView的引用，和Appbar的原始高度
     *
     * @param abl
     */
    private fun init(abl: AppBarLayout) {
        abl.clipChildren = false
        mAppbarHeight = abl.height
        mImageView = abl.findViewById(R.id.zoom_image)
        if (mImageView != null) {
            mImageViewHeight = mImageView!!.height
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
        isAnimate = true
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    /**
     * 在这里做具体的滑动处理
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     * @param type
     */
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {

        MLog.e(
            "位置",
            "dx:$dx --- dy:$dy child.bottom:${child.bottom}  mAppbarHeight:$mAppbarHeight topImageMinHeight:$mImageViewHeight"
        )

        if (mImageView != null && child.bottom >= mAppbarHeight && dy < 0 && type == ViewCompat.TYPE_TOUCH) {//
            zoomHeaderImageView(child, dy)
        } else {
            if (mImageView != null && child.bottom > mAppbarHeight && dy > 0 && type == ViewCompat.TYPE_TOUCH) {//
                consumed[1] = dy
                zoomHeaderImageView(child, dy)
            } else {
                if (valueAnimator == null || !valueAnimator!!.isRunning) {
                    super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
                }

            }
        }

    }


    /**
     * 对ImageView进行缩放处理，对AppbarLayout进行高度的设置
     *
     * @param abl
     * @param dy
     */
    private fun zoomHeaderImageView(abl: AppBarLayout, dy: Int) {
        mTotalDy += -dy
        mTotalDy = Math.min(mTotalDy, MAX_ZOOM_HEIGHT)
        mScaleValue = Math.max(1f, 1f + mTotalDy / MAX_ZOOM_HEIGHT)
        MLog.e("scale", "mScaleValue:$mScaleValue mTotalDy:$mTotalDy")
        mImageView!!.scaleX = mScaleValue
        mImageView!!.scaleY = mScaleValue
        mLastBottom = mAppbarHeight + (mImageViewHeight / 2 * (mScaleValue - 1)).toInt()
        abl.bottom = mLastBottom
    }


    /**
     * 处理惯性滑动的情况
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityY > 100) {
            isAnimate = false
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }


    /**
     * 滑动停止的时候，恢复AppbarLayout、ImageView的原始状态
     *
     * @param coordinatorLayout
     * @param abl
     * @param target
     * @param type
     */
    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        recovery(abl)
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
    }

    /**
     * 通过属性动画的形式，恢复AppbarLayout、ImageView的原始状态
     *
     * @param abl
     */
    private fun recovery(abl: AppBarLayout) {
        if (mTotalDy > 0) {
            mTotalDy = 0f
            if (isAnimate) {
                valueAnimator = ValueAnimator.ofFloat(mScaleValue, 1f).setDuration(220)
                valueAnimator!!.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    MLog.e("Update", "value:${animation.animatedValue}  animatedFraction:${animation.animatedFraction}")
                    mImageView!!.scaleX = value
                    mImageView!!.scaleY = value
                    abl.bottom = (mLastBottom - (mLastBottom - mAppbarHeight) * animation.animatedFraction).toInt()
                }
                valueAnimator!!.start()
            } else {
                mImageView!!.scaleX = 1f
                mImageView!!.scaleY = 1f
                abl.bottom = mAppbarHeight
            }
        }
    }
}
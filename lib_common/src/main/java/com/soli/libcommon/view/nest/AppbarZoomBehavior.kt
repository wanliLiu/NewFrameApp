package com.soli.libcommon.view.nest

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import com.soli.libcommon.util.MLog
import kotlin.math.max
import kotlin.math.min

/*
 * @author soli
 * @Time 2019/1/13 22:15
 */
class AppbarZoomBehavior : AppBarLayout.Behavior {

    private val Tag = "AppbarZoomBehavior"

    private val TAG = "zoomImage"
    private val TAG_MIDDLE = "middle"
    private val TAG_STICK = "stick"

    private var mImageView: View? = null
    private var middleLayout: View? = null
    private var stickLayout: View? = null

    private var mAppbarHeight = 0//记录AppbarLayout原始高度
    private var mImageViewHeight = 0//记录ImageView原始高度
    private var mMiddleHeight = 0
    private var mStickLayoutHeight = 0

    private var mTotalDy = 0f//手指在Y轴滑动的总距离
    private var mScaleValue = 0f//图片缩放比例
    private var mLastBottom = 0//Appbar的变化高度

    private var isInit = false

    private val MAX_ZOOM_HEIGHT = 800f//放大最大高度

    private var isAnimate: Boolean = false//是否做动画标志

    private var valueAnimator: ValueAnimator? = null

    private var lastRefreshTime = 0L
    private var refresDuration = 10//ms

    constructor()


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    override fun onLayoutChild(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        abl: AppBarLayout,
        layoutDirection: Int
    ): Boolean {
        val handled = super.onLayoutChild(parent, abl, layoutDirection)
        if (!isInit)
            init(abl)
        return handled
    }

    /**
     * 进行初始化操作，在这里获取到ImageView的引用，和Appbar的原始高度
     *
     * @param abl
     */
    private fun init(abl: AppBarLayout) {
        isInit = true
        abl.clipChildren = false
        mAppbarHeight = abl.height
        middleLayout = abl.findViewWithTag(TAG_MIDDLE)
        stickLayout = abl.findViewWithTag(TAG_STICK)
        mImageView = abl.findViewWithTag(TAG)
        if (mImageView != null) {
            mImageViewHeight = mImageView!!.height
        }

        if (middleLayout != null) {
            mMiddleHeight = middleLayout!!.height
        }

        if (stickLayout != null) {
            mStickLayoutHeight = stickLayout!!.height
        }
    }

    override fun onStartNestedScroll(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        isAnimate = true

        if (target is TransparentTouchEvent) return true

        return super.onStartNestedScroll(
            parent,
            child,
            directTargetChild,
            target,
            nestedScrollAxes,
            type
        )
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
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {

        MLog.e(
            Tag,
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
                    super.onNestedPreScroll(
                        coordinatorLayout,
                        child,
                        target,
                        dx,
                        dy,
                        consumed,
                        type
                    )
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
        mTotalDy = min(mTotalDy, MAX_ZOOM_HEIGHT)
        mScaleValue = max(1f, 1f + mTotalDy / MAX_ZOOM_HEIGHT)
        MLog.e(Tag, "mScaleValue:$mScaleValue mTotalDy:$mTotalDy")
        mImageView!!.scaleX = mScaleValue
        mImageView!!.scaleY = mScaleValue
        mLastBottom = mAppbarHeight + (mImageViewHeight / 2 * (mScaleValue - 1)).toInt()
        abl.bottom = mLastBottom

        middleLayout?.top =
            if (stickLayout != null) mLastBottom - mStickLayoutHeight - mMiddleHeight else mLastBottom - mMiddleHeight
        middleLayout?.bottom =
            if (stickLayout != null) mLastBottom - mStickLayoutHeight else mLastBottom

        stickLayout?.top = mLastBottom - mStickLayoutHeight
        stickLayout?.bottom = mLastBottom
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
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
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
    override fun onStopNestedScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
        abl: AppBarLayout,
        target: View,
        type: Int
    ) {
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

                valueAnimator =
                    ValueAnimator.ofFloat(mScaleValue, 1f).setDuration(220)
                valueAnimator!!.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    MLog.e(
                        Tag,
                        "value:${animation.animatedValue}  animatedFraction:${animation.animatedFraction}"
                    )

                    mImageView!!.scaleX = value
                    mImageView!!.scaleY = value

                    val fraction =
                        mLastBottom - (mLastBottom - mAppbarHeight) * animation.animatedFraction

                    abl.bottom = fraction.toInt()

                    middleLayout?.top =
                        (if (stickLayout != null) fraction - mStickLayoutHeight - mMiddleHeight else fraction - mMiddleHeight).toInt()
                    middleLayout?.bottom =
                        (if (stickLayout != null) fraction - mStickLayoutHeight else fraction).toInt()

                    stickLayout?.top = (fraction - mStickLayoutHeight).toInt()
                    stickLayout?.bottom = fraction.toInt()
                }
                valueAnimator!!.start()
            } else {
                mImageView!!.scaleX = 1f
                mImageView!!.scaleY = 1f
                abl.bottom = mAppbarHeight
                middleLayout?.top =
                    if (stickLayout != null) mAppbarHeight - mStickLayoutHeight - mMiddleHeight else mAppbarHeight - mMiddleHeight
                middleLayout?.bottom =
                    if (stickLayout != null) mAppbarHeight - mStickLayoutHeight else mAppbarHeight
                stickLayout?.top = mAppbarHeight - mStickLayoutHeight
                stickLayout?.bottom = mAppbarHeight
            }
        }
    }
}
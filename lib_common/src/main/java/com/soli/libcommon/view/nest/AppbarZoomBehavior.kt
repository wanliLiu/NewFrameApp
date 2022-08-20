package com.soli.libcommon.view.nest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
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

    private val MAX_ZOOM_HEIGHT = 800f//放大最大高度

    private var isAnimate: Boolean = false//是否做动画标志

    private var valueAnimator: ValueAnimator? = null

    var dealTool: DealTransparentTouchEvent? = null
    private var lastVelocityY = 0f

    constructor()


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    override fun onLayoutChild(
        parent: CoordinatorLayout,
        abl: AppBarLayout,
        layoutDirection: Int
    ): Boolean {
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

        middleLayout = abl.findViewWithTag(TAG_MIDDLE)
        stickLayout = abl.findViewWithTag(TAG_STICK)
        mImageView = abl.findViewWithTag(TAG)

        mImageViewHeight = mImageView?.height ?: 0
        mMiddleHeight = middleLayout?.height ?: 0
        mStickLayoutHeight = stickLayout?.height ?: 0
    }

    //这个地方很重要，不用父的，也就是不走之前的那种滑动，采用嵌套传递的那种滑动
    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        ev: MotionEvent
    ): Boolean {
        return false
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
        MLog.e(Tag, "onStartNestedScroll-----> isAnimate :$isAnimate")

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
     *
     */
    private fun canChildScrollUp(view: View) =
        when (view) {
            is AppBarLayout -> view.top != 0
            is CoordinatorLayout -> view.childCount > 0 && view.getChildAt(0).top != 0
            else -> view.canScrollVertically(-1)
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
        val isTop = !canChildScrollUp(target)

        MLog.e(
            Tag,
            "onNestedPreScroll--> type :$type  dy:$dy child.bottom:${child.bottom}  mAppbarHeight:$mAppbarHeight topImageMinHeight:$mImageViewHeight targetView:${target::class.java.simpleName} isTop : $isTop"
        )

//        valueAnimator?.cancel()

        if (isTop && mImageView != null && child.bottom >= mAppbarHeight && dy < 0 && type == ViewCompat.TYPE_TOUCH) {//
            zoomHeaderImageView(child, dy)
            consumed[1] = dy
        } else {
            if (isTop && mImageView != null && child.bottom > mAppbarHeight && dy > 0 && type == ViewCompat.TYPE_TOUCH) {//
                zoomHeaderImageView(child, dy)
                consumed[1] = dy
            } else {
                if (valueAnimator == null || !valueAnimator!!.isRunning) {
                    MLog.e(Tag, " super.onNestedPreScroll")
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


    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        MLog.e(
            Tag,
            "onNestedScroll-->type :$type  dyUnconsumed :$dyUnconsumed"
        )
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
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
        MLog.e(Tag, "zoomHeaderImageView----->mScaleValue:$mScaleValue mTotalDy:$mTotalDy")
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
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityY > 100) {
            isAnimate = false
        }
        lastVelocityY = velocityY
        MLog.e(
            Tag,
            "onNestedPreFling --> child : ${child.javaClass.simpleName}  target : ${target.javaClass.simpleName}  velocityX ：$velocityX  velocityY : $velocityY  isAnimate:$isAnimate"
        )
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
        coordinatorLayout: CoordinatorLayout,
        abl: AppBarLayout,
        target: View,
        type: Int
    ) {
        MLog.e(Tag, "onStopNestedScroll--->type :$type  isAnimate:$isAnimate")
        recovery(abl, type)

        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
    }

    /**
     * 通过属性动画的形式，恢复AppbarLayout、ImageView的原始状态
     *
     * @param abl
     */
    private fun recovery(abl: AppBarLayout, type: Int) {
        MLog.e(Tag, "recovery ----->mTotalDy :$mTotalDy")
        if (mTotalDy > 0) {
            mTotalDy = 0f
            if (isAnimate) {
                valueAnimator = ValueAnimator.ofFloat(mScaleValue, 1f).setDuration(220)
                valueAnimator!!.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    MLog.e(
                        Tag,
                        "recovery------> value:${animation.animatedValue}  animatedFraction:${animation.animatedFraction}"
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
                valueAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        dealTool?.apply {
                            MLog.e(Tag, "end up fling type :$type lastVelocityY:$lastVelocityY")
                            if (lastVelocityY != 0f) {
                                fling(lastVelocityY.toInt())
                            }
                            lastVelocityY = 0f
                        }
                    }
                })
                valueAnimator!!.start()
            } else {
                //永远不会执行这里
                MLog.e(Tag, "recovery---结束未执行动画")
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
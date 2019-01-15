package com.soli.libCommon.view

import android.animation.ValueAnimator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.math.MathUtils
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.soli.libCommon.R
import com.soli.libCommon.util.MLog


/*
 *
 * @author soli
 * @Time 2018/12/20 21:59
 */
class ZoomAppBarLayoutBehavior : FixAppBarLayoutBehavior {

    private var topImage: View? = null
    private var topImageMinHeight = 0//记录ImageView原始高度

    private var mAppbarHeight = 0//记录AppbarLayout原始高度


    private val MAX_ZOOM_HEIGHT = 500//放大最大高度
    private var mTotalDy = 0//手指在Y轴滑动的总距离
    private var mScaleValue = 0f//图片缩放比例
    private var mLastBottom = 0//Appbar的变化高度

    private var isAnimate: Boolean = false//是否做动画标志

    constructor() : super()

    constructor(context: Context? = null, attrs: AttributeSet? = null) : super(context, attrs)


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
        mAppbarHeight = abl.measuredHeight
        topImage = abl.findViewById(R.id.zoom_image)
        if (topImage != null) {
            topImageMinHeight = topImage!!.measuredHeight
        }
    }

    /**
     * 是否处理嵌套滑动
     *
     * @param parent
     * @param child
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes
     * @param type
     * @return
     */
    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        isAnimate = true
        return true
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
            "dx:$dx --- dy:$dy child.bottom:${child.bottom}  mAppbarHeight:$mAppbarHeight topImageMinHeight:$topImageMinHeight"
        )
        if (topImage != null && child.bottom >= mAppbarHeight && dy < 0 && type == ViewCompat.TYPE_TOUCH) {
            zoomHeaderImageView(child, dy)
        } else {
            if (topImage != null && child.bottom > mAppbarHeight && dy > 0 && type == ViewCompat.TYPE_TOUCH) {
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
        changeTopImageHeight(MathUtils.clamp(topImageMinHeight + mTotalDy, topImageMinHeight, topImageMinHeight * 2))
//
//        mTotalDy = Math.min(mTotalDy, MAX_ZOOM_HEIGHT)
//        mScaleValue = Math.max(1f, 1f + mTotalDy / MAX_ZOOM_HEIGHT)
//        ViewCompat.setScaleX(topImage, mScaleValue)
//        ViewCompat.setScaleY(topImage, mScaleValue)
//        mLastBottom = mAppbarHeight + (topImageMinHeight / 2 * (mScaleValue - 1)).toInt()
//        abl.bottom = mLastBottom
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

    var valueAnimator: ValueAnimator? = null

    /**
     * 通过属性动画的形式，恢复AppbarLayout、ImageView的原始状态
     *
     * @param abl
     */
    private fun recovery(abl: AppBarLayout) {
        releaseToInitPosition()
//        if (mTotalDy > 0) {
//            mTotalDy = 0
//            if (isAnimate) {
//                valueAnimator = ValueAnimator.ofFloat(mScaleValue, 1f).setDuration(220)
//                valueAnimator!!.addUpdateListener { animation ->
//                    val value = animation.animatedValue as Float
//                    ViewCompat.setScaleX(topImage, value)
//                    ViewCompat.setScaleY(topImage, value)
//                    abl.bottom = (mLastBottom - (mLastBottom - mAppbarHeight) * animation.animatedFraction).toInt()
//                }
//                valueAnimator!!.start()
//            } else {
//                ViewCompat.setScaleX(topImage, 1f)
//                ViewCompat.setScaleY(topImage, 1f)
//                abl.bottom = mAppbarHeight
//            }
//        }
    }


    private fun changeTopImageHeight(height: Int) {
        topImage?.apply {
            layoutParams.height = height
            requestLayout()
        }
    }

    /**
     *
     */
    private fun releaseToInitPosition() {
        topImage?.apply {
            if (mTotalDy > 0) {
                val height = MathUtils.clamp(topImageMinHeight + mTotalDy, topImageMinHeight, topImageMinHeight * 2)
                mTotalDy = 0
                if (isAnimate) {
                    if (height > topImageMinHeight) {
                        valueAnimator = ValueAnimator.ofInt(height, topImageMinHeight)
                        valueAnimator!!.addUpdateListener { animation ->
                            run {
                                layoutParams.height = animation.animatedValue as Int
                                requestLayout()
                            }
                        }
                        valueAnimator!!.start()
                    }
                } else {
                    changeTopImageHeight(height)
                }
            }

        }
    }
}

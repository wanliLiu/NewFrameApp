package com.soli.libCommon.view

import android.annotation.TargetApi
import android.content.Context
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.soli.libCommon.R
import com.soli.libCommon.util.StatusBarUtil

/**
 *  事件的驱动来源于AppBarlayout, 事件触发者--->CoordinatorLayout-->AppBarLayout-->CollapsingToolbarLayout
 * @author Soli
 * @Time 2018/11/15 13:44
 */
class CollapsingToolbarLayout : FrameLayout {

    private var isForSpecailTitle = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private var mOnOffsetChangedListener: AppBarLayout.OnOffsetChangedListener? = null

    /**
     *
     */
    private fun init(ctx: Context, attrs: AttributeSet?) {
        attrs?.apply {
            val a = ctx.obtainStyledAttributes(attrs, R.styleable.CollapsingToolbarLayout)
            isForSpecailTitle = a.getBoolean(R.styleable.CollapsingToolbarLayout_isForSpecailTitle, isForSpecailTitle)
            a.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Add an OnOffsetChangedListener if possible
        val parent = parent
        if (parent is AppBarLayout) {

            val statusBarHeight = if (isForSpecailTitle) 0 else StatusBarUtil.getStatusBarHeight(context)
            val toolbar = findViewById<ViewGroup>(R.id.barRoot)
                ?: throw IllegalArgumentException("这个布局必须包含 com.taihe.libCommon.view.root.Toolbar 一起使用")

            toolbar.setPadding(0, statusBarHeight, 0, 0)

            val height = context.resources.getDimensionPixelOffset(R.dimen.toolbar_height) + statusBarHeight

            minimumHeight = height

            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = OffsetUpdateListener()
            }

            parent.addOnOffsetChangedListener(mOnOffsetChangedListener)

            //固定视图的特殊处理
            findViewById<ViewGroup>(R.id.specialHead)?.apply {
                setPadding(0, height, 0, 0)
            }
        }
    }


    override fun onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        val parent = parent
        if (mOnOffsetChangedListener != null && parent is AppBarLayout) {
            parent.removeOnOffsetChangedListener(mOnOffsetChangedListener)
        }

        super.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // Update our child view offset helpers. This needs to be done after the title has been
        // setup, so that any Toolbars are in their original position
        for (i in 0 until childCount) {
            getViewOffsetHelper(getChildAt(i)).onViewLayout()
        }
    }

    /**
     * @param view
     * @return
     */
    private fun getViewOffsetHelper(view: View): ViewOffsetHelper {
        val tag = view.getTag(R.id.view_offset_helper)
        var offsetHelper: ViewOffsetHelper? = if (tag != null) tag as ViewOffsetHelper else null
        if (offsetHelper == null) {
            offsetHelper = ViewOffsetHelper(view)
            view.setTag(R.id.view_offset_helper, offsetHelper)
        }
        return offsetHelper
    }


    override fun checkLayoutParams(p: ViewGroup.LayoutParams) = p is LayoutParams


    override fun generateDefaultLayoutParams(): FrameLayout.LayoutParams =
        LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

    override fun generateLayoutParams(attrs: AttributeSet): FrameLayout.LayoutParams = LayoutParams(context, attrs)

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): FrameLayout.LayoutParams = LayoutParams(p)


    private class LayoutParams : FrameLayout.LayoutParams {

        companion object {

            const val DEFAULT_PARALLAX_MULTIPLIER = 0.5f

            /**
             * The view will act as normal with no collapsing behavior.
             */
            const val COLLAPSE_MODE_OFF = 0

            /**
             * The view will pin in place until it reaches the bottom of the
             * [CollapsingToolbarLayout].
             */
            const val COLLAPSE_MODE_PIN = 1

            /**
             * The view will scroll in a parallax fashion. See [.setParallaxMultiplier]
             * to change the multiplier used.
             */
            const val COLLAPSE_MODE_PARALLAX = 2
        }

        var collapseMode = COLLAPSE_MODE_OFF

        var parallaxMultiplier = DEFAULT_PARALLAX_MULTIPLIER

        /**
         * @hide
         */
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.CollapsingToolbarLayout)
            collapseMode = a.getInt(R.styleable.CollapsingToolbarLayout_collapseMode, COLLAPSE_MODE_OFF)
            parallaxMultiplier = a.getFloat(
                R.styleable.CollapsingToolbarLayout_collapseParallaxMultiplier,
                DEFAULT_PARALLAX_MULTIPLIER
            )
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)

        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)

        constructor(p: ViewGroup.LayoutParams) : super(p)

        constructor(source: ViewGroup.MarginLayoutParams) : super(source)

        @RequiresApi(19)
        @TargetApi(19)
        constructor(source: FrameLayout.LayoutParams) : super(source)

    }

    /**
     * @param child
     * @return
     */
    internal fun getMaxOffsetForPinChild(child: View): Int {
        val offsetHelper = getViewOffsetHelper(child)
        val lp = child.layoutParams as LayoutParams
        return (height - offsetHelper.layoutTop - child.height - lp.bottomMargin)
    }

    /**
     * @param amount
     * @param low
     * @param high
     * @return
     */
    private fun constrain(amount: Int, low: Int, high: Int): Int {
        return if (amount < low) low else if (amount > high) high else amount
    }

    /**
     *
     */
    private inner class OffsetUpdateListener : AppBarLayout.OnOffsetChangedListener {

        override fun onOffsetChanged(layout: AppBarLayout, verticalOffset: Int) {
            for (index in 0 until childCount) {
                val child = getChildAt(index)
                val lp = child.layoutParams as LayoutParams
                val offsetHelper = getViewOffsetHelper(child)
                when (lp.collapseMode) {
                    LayoutParams.COLLAPSE_MODE_PIN -> offsetHelper.setTopAndBottomOffset(
                        constrain(
                            -verticalOffset,
                            0,
                            getMaxOffsetForPinChild(child)
                        )
                    )
                    LayoutParams.COLLAPSE_MODE_PARALLAX -> offsetHelper.setTopAndBottomOffset(Math.round(-verticalOffset * lp.parallaxMultiplier))
                }
            }
            //            int scrollRangle = layout.getTotalScrollRange();
            //            Log.e("verticalOffset", String.valueOf(verticalOffset));
            //            double alpha = Math.abs(verticalOffset) * 1.0 / scrollRangle * 1.0;

        }
    }
}

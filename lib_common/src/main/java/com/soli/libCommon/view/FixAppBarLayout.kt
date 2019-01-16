package com.soli.libCommon.view

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

/**
 *用这个，添加处理惯性滑动引起的问题
 * @author Soli
 * @Time 2018/12/21 13:34
 */
class FixAppBarLayout : AppBarLayout, CoordinatorLayout.AttachedBehavior {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
//        return FixAppBarLayoutBehavior()
        return AppbarZoomBehavior()
//        return ZoomAppBarLayoutBehavior()
    }
}
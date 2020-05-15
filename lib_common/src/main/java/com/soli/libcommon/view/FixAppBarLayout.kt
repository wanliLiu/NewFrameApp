package com.soli.libcommon.view

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

/**
 *
 * http://liuling123.com/2016/01/overscroll-appBarLayout-behavior.html
 *
 *
 *用这个，添加处理惯性滑动引起的问题
 * @author Soli
 * @Time 2018/12/21 13:34
 */
class FixAppBarLayout : AppBarLayout, CoordinatorLayout.AttachedBehavior {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun getBehavior(): CoordinatorLayout.Behavior<AppBarLayout> {
//        return FixAppBarLayoutBehavior()
        return AppbarZoomBehavior()
//        return ZoomAppBarLayoutBehavior()
    }
}
package com.soli.libcommon.view

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.soli.libcommon.R

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


//    init {
        //设置背景色为透明，不然会用colorPrimary，AppBarLayout默认用colorPrimary
//        setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
//    }


    override fun getBehavior(): CoordinatorLayout.Behavior<AppBarLayout> {
//        return FixAppBarLayoutBehavior()
        return AppbarZoomBehavior()
//        return ZoomAppBarLayoutBehavior()
    }
}
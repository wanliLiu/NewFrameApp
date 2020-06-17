package com.soli.libcommon.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soli.libcommon.util.MLog
import me.yokeyword.fragmentation.SwipeBackLayout


/**
 *  如果用于作为Framgnet框架的话，就需要顶部的状态栏
 * @author Soli
 * @Time 2020/4/20 14:33
 */
abstract class BaseSwipeBackFragment : BaseFragment() {
    //该Fragment是否支持滑动退出
    open fun needSwipeBack() = true

    //拖动的回调，主要用于在首页的时候，对mini bar做操作
    var dragStateCallBack: ((Boolean, Float) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dealNeedSwipeBackAction(
            super.onCreateView(
                inflater,
                container,
                savedInstanceState
            )!!
        )
    }

    /**
     *
     */
    private fun dealNeedSwipeBackAction(view: View): View =
        if (needSwipeBack()) {
            addSwipeBackListener()
            attachToSwipeBack(view)
        } else {
            setSwipeBackEnable(false)
            view
        }

    /**
     *
     */
    private fun addSwipeBackListener() {
        swipeBackLayout?.addSwipeListener(object : SwipeBackLayout.OnSwipeListener {
            override fun onEdgeTouch(oritentationEdgeFlag: Int) = Unit
            override fun onDragScrolled(scrollPercent: Float) {
                if (dragStateCallBack != null) {
                    val afterValue = when {
                        scrollPercent >= 1f -> 1f
                        scrollPercent <= 0f -> 0f
                        else -> scrollPercent
                    }
                    MLog.d("onDragScrolled", "$scrollPercent --- $afterValue")
                    dragStateCallBack?.invoke(false, afterValue)
                }
            }

            override fun onDragStateChange(state: Int) {
                if (dragStateCallBack != null) {
                    if (state == SwipeBackLayout.STATE_FINISHED)
                        dragStateCallBack?.invoke(true, 1.0f)
                }
            }
        })
    }

}
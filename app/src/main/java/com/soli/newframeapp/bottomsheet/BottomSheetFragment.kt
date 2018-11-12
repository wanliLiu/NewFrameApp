package com.soli.newframeapp.bottomsheet

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.soli.newframeapp.R


/**
 *
 * @author Soli
 * @Time 2018/11/12 13:47
 */
class BottomSheetFragment : BottomSheetDialogFragment() {

    /**
     * 顶部向下偏移量
     */
    private var topOffset = 0
    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BottomSheetFragment() }
    }

    override fun getTheme() = R.style.TransBottomSheetDialogStyle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_bottomsheet, null)
    }

    override fun onStart() {
        super.onStart()
        // 设置软键盘不自动弹出
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.delegate.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.height = getHeight()
            behavior = BottomSheetBehavior.from(bottomSheet)
            // 初始为展开状态
            behavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * 获取屏幕高度
     *
     * @return height
     */
    private fun getHeight(): Int {
        var height = 1920
        if (context != null) {
            val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            if (wm != null) {
                // 使用Point已经减去了状态栏高度
                wm.defaultDisplay.getSize(point)
                height = point.y - getTopOffset()
            }
        }
        return height
    }

    fun getTopOffset(): Int {
        return topOffset
    }

    fun setTopOffset(topOffset: Int) {
        this.topOffset = topOffset
    }

    fun getBehavior(): BottomSheetBehavior<FrameLayout> {
        return behavior!!
    }


}
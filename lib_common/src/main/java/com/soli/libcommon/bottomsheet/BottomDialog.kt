package com.soli.libcommon.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.soli.libcommon.R
import com.soli.libcommon.bottomsheet.behavior.SpecialBottomSheetBehavior
import com.soli.libcommon.bottomsheet.behavior.SpecialBottomSheetDialog
import com.soli.libcommon.util.StatusBarUtil


/**
 *
 * @author Soli
 * @Time 2018/11/12 15:09
 */
class BottomDialog(mctx: Context, theme: Int) : SpecialBottomSheetDialog(mctx, theme) {

    private val ctx = mctx
    //默认是内容的高度
    private var isWrapConent = true

    private var topOffset = 0
    //默认可以拖动
    private var canDrag = true

    constructor(ctx: Context) : this(ctx, 0)


    init {

        topOffset = StatusBarUtil.getStatusBarHeight(ctx) +
                ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height)

    }


    override fun wrapInBottomSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {
        return setDialogBackGround(super.wrapInBottomSheet(layoutResId, view, params))
    }

    /**
     * 设置背景原色
     */
    private fun setDialogBackGround(view: View): View {

        optionCanotDrag()

        behavior.state = SpecialBottomSheetBehavior.STATE_EXPANDED
        //关闭的时候跳过折叠
//        behavior.skipCollapsed = true
        //可以隐藏
        behavior.isHideable = true

        val bottomSheet = view.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        bottomSheet?.apply {
            setBackgroundColor(ContextCompat.getColor(ctx,android.R.color.transparent))
            if (!isWrapConent && topOffset > 0) {
                val params = layoutParams as CoordinatorLayout.LayoutParams
                params.height = getContentHeight() - topOffset
                behavior.peekHeight = params.height
            }
        }

        return view
    }

    /**
     * 获取视图显示的高度
     */
    private fun getContentHeight(): Int {
        var contentHeight = (ctx as Activity).window.decorView.height
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ctx.findViewById<View>(android.R.id.navigationBarBackground)?.apply {
                if (visibility == View.VISIBLE)
                    contentHeight -= height
            }
        }

        return contentHeight
    }

    /**
     *默认高度是到导航栏下面
     */
    fun topOffsetDefault() {
        isWrapConent = false
    }

    /**
     * 设置到顶部的偏移量
     */
    fun setTopOffset(offset: Int) {
        topOffset = offset
        isWrapConent = false
    }


    /**
     *具体设置不能拖拽
     */
    private fun optionCanotDrag() {
        if (!canDrag) {
            behavior.setBottomSheetCallback(object : SpecialBottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == SpecialBottomSheetBehavior.STATE_DRAGGING)
                        behavior.state = SpecialBottomSheetBehavior.STATE_EXPANDED
                }
            })
        }
    }

    /**
     * 设置不可以拖拽
     */
    fun setCanotDrag() {
        canDrag = false
    }
}
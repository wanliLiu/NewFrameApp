package com.soli.libCommon.bottomSheet

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.soli.libCommon.R
import com.soli.libCommon.util.StatusBarUtil


/**
 *
 * @author Soli
 * @Time 2018/11/12 15:09
 */
class BottomDialog(mctx: Context) : BottomSheetDialog(mctx) {

    private val ctx = mctx
    //默认是内容的宽度
    private var isWrapConent = true

    private var topOffset = 0
    //默认可以拖动
    private var canDrag = true

    init {

        topOffset = StatusBarUtil.getStatusBarHeight(ctx) +
                ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height)

    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        setDialogBackGround()
    }

    override fun setContentView(layoutResId: Int) {
        super.setContentView(layoutResId)
        setDialogBackGround()
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        setDialogBackGround()
    }

    /**
     * 设置背景原色
     */
    private fun setDialogBackGround() {
        val bottomSheet = delegate.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.apply {
            setBackgroundColor(ctx.resources.getColor(android.R.color.transparent))
            if (!isWrapConent && topOffset > 0) {
                val params = layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
                params.height = getContentHeight() - topOffset
            }
        }
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
     *
     */
    private fun prepareForShow() {
        //默认不展示
        getBehavior().peekHeight = 0
        //关闭的时候跳过折叠
        getBehavior().skipCollapsed = true
        //可以隐藏
        getBehavior().isHideable = true
    }

    override fun show() {
        super.show()
        prepareForShow()
        getBehavior().state = BottomSheetBehavior.STATE_EXPANDED
        optionCanotDrag()
    }

    /**
     *具体设置不能拖拽
     */
    private fun optionCanotDrag() {
        if (!canDrag) {
            val behavior = getBehavior()
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING)
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            })
        }
    }

    /**
     *
     */
    fun getBehavior(): BottomSheetBehavior<FrameLayout> =
        BottomSheetBehavior.from(delegate.findViewById(com.google.android.material.R.id.design_bottom_sheet))

    /**
     * 设置不可以拖拽
     */
    fun setCanotDrag() {
        canDrag = false
    }
}
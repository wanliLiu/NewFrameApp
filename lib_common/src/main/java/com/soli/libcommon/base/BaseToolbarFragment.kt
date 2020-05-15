package com.soli.libcommon.base

import android.view.View


/**
 *  作为Framgnet框架的话，每个页面的独立Fragment 包括顶部状态栏、滑动退出、加载等等
 * @author Soli
 * @Time 2020/4/20 14:33
 */
abstract class BaseToolbarFragment : BaseSwipeBackFragment() {
    override fun needTopToolbar() = true

    override fun setContentViews(view: View) {
        super.setContentViews(view)
        rootView.judgeToolBarOffset()
    }

}
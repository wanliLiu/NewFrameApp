package com.soli.newframeapp.fragment

import android.view.View


/**
 *  如果用于作为Framgnet框架的话，就需要顶部的状态栏
 * @author Soli
 * @Time 2020/4/20 14:33
 */
abstract class BaseToolbarFragment : BaseAnimationFragment() {

    override fun needTopToolbar() = true

    override fun setContentViews(view: View) {
        super.setContentViews(view)
        rootView.judgeToolBarOffset()
    }

}
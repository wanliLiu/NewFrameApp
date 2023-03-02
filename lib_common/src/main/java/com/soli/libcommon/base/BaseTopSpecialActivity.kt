package com.soli.libcommon.base

import androidx.viewbinding.ViewBinding
import com.soli.libcommon.util.StatusBarUtil

/**
 *顶部有图片 需要滑动处理用这个基类，这个主要不要弹出键盘输入
 * @author Soli
 * @Time 2018/11/15 11:16
 */
abstract class BaseTopSpecialActivity<Binding : ViewBinding> : BaseActivity<Binding>() {

    override fun needTopToolbar() = false

    override fun dealCusotomStatus(color: Int, statusBarAlpha: Int): Boolean {
        StatusBarUtil.setTransparentForWindow(this)
        return true
    }


    override fun initView() {
        rootView.reInitView()
    }

}
package com.soli.libcommon.base

import androidx.viewbinding.ViewBinding
import com.soli.libcommon.util.StatusBarUtil

/**
 *  目前暂定为，那种，单Activity,多Fragment 底层基类
 * @author Soli
 * @Time 2020/5/20 14:27
 */
abstract class BaseMultiFragmentActivity<Binding : ViewBinding> : BaseActivity<Binding>() {

    override fun needTopToolbar() = false

    override fun dealCusotomStatus(color: Int, statusBarAlpha: Int): Boolean {
        StatusBarUtil.setTransparentForWindow(this)
        return true
    }
}
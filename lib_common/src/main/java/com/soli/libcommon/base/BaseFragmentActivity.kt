package com.soli.libcommon.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.soli.libcommon.R
import com.soli.libcommon.util.StatusBarUtil

/**
 *  Fragment作为底层框架的实现，就是代替Activity
 *  用来切换和管理各个Fragment
 * @author Soli
 * @Time 2020/4/20 13:55
 */
abstract class BaseFragmentActivity : BaseActivity() {

    override fun needTopToolbar() = false

    override fun needSliderActivity() = false

    override fun dealCusotomStatus(color: Int, statusBarAlpha: Int): Boolean {
        StatusBarUtil.setTransparentForWindow(this)
        return true
    }

    override fun getContentView() = R.layout.activity_root_fragment_view


    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.fragments.isEmpty())
            super.onBackPressed()
    }
}
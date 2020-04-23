package com.soli.newframeapp.fragment

import android.os.Bundle
import android.util.Log
import com.soli.libcommon.BuildConfig
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.StatusBarUtil
import me.yokeyword.fragmentation.Fragmentation
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

/**
 * @author Soli
 * @Time 2020/4/20 13:55
 */
abstract class BaseLaunchUI : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        initFragmentation()
        super.onCreate(savedInstanceState)
    }

    private fun initFragmentation() {
        Fragmentation.builder()
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(BuildConfig.DEBUG)
            .handleException {
                Log.e("fragment", it.message)
            }
            .install()
    }

    override fun needTopToolbar() = false

    override fun needSliderActivity() = false

    override fun dealCusotomStatus(color: Int, statusBarAlpha: Int): Boolean {
        StatusBarUtil.setTransparentForWindow(this)
        return true
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

}
package com.soli.newframeapp.fragment

import android.os.Bundle
import android.util.Log
import com.soli.libcommon.base.BaseFragmentationActivity
import com.soli.libcommon.util.StatusBarUtil
import com.soli.newframeapp.BuildConfig
import com.soli.newframeapp.R
import me.yokeyword.fragmentation.Fragmentation
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

/**
 * @author Soli
 * @Time 2020/4/20 13:55
 */
abstract class BaseLaunchUI : BaseFragmentationActivity() {


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
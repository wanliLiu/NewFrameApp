package com.soli.newframeapp.fragment

import android.os.Bundle
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.StatusBarUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.greenrobot.eventbus.EventBus

/**
 * @author Soli
 * @Time 2020/4/20 13:55
 */
abstract class BaseLaunchUI : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch { EventBus.getDefault().register(ctx) }
        super.onCreate(savedInstanceState)
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(ctx)
    }

}
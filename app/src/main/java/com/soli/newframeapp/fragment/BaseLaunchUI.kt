package com.soli.newframeapp.fragment

import android.os.Bundle
import com.soli.libcommon.base.BaseMultiFragmentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.greenrobot.eventbus.EventBus

/**
 * @author Soli
 * @Time 2020/4/20 13:55
 */
abstract class BaseLaunchUI : BaseMultiFragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalScope.launch { EventBus.getDefault().register(ctx) }
        super.onCreate(savedInstanceState)
    }

    override fun needSliderActivity() = false

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultHorizontalAnimator()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(ctx)
    }

}
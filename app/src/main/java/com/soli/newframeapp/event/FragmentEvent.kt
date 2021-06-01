package com.soli.newframeapp.event

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.soli.libcommon.base.common.CommonActivity
import com.soli.libcommon.util.ToastUtils
import com.soli.newframeapp.fragment.BaseLaunchUI
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.EventBus

/**
 *
 * @author Soli
 * @Time 2020/4/30 17:29
 */
data class OpenFragmentEvent(
    val fragment: SupportFragment = SupportFragment(),
    val launchMode: Int = ISupportFragment.STANDARD,
    val isPopEvent: Boolean = false
)

/**
 *
 */
inline fun SupportFragment.popFragment() {
    requireContext().popFragment()
}

/**
 *
 */
inline fun Context.popFragment() {

    if (this is BaseLaunchUI)
        EventBus.getDefault().post(OpenFragmentEvent(isPopEvent = true))

    if (this is SupportActivity)
        onBackPressed()
}

/**
 *
 */
fun SupportFragment.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD,
    //重新开一个Activity来装载Fragment
    //重新开一个Activity来装载Fragment
    useEventBus: Boolean = true,
    newActivity: Boolean = false
) {
    requireActivity().openFragment(fragment, launchMode, useEventBus,newActivity)
}


inline fun <reified T : SupportFragment> Context.startFragment(params: Bundle? = null) {
    CommonActivity.startFragment(this, T::class.java.name, params)
}


/**
 *
 */
fun Context.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD,
    //重新开一个Activity来装载Fragment
    useEventBus: Boolean = true,
    newActivity: Boolean = false
) {
    when {
//        newActivity -> CommonActivity.startFragment(this, fragment)
        this is CommonActivity -> start(fragment, launchMode)
        useEventBus -> EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
        else -> {
            ToastUtils.showShortToast("你要要开那个地方")
        }
    }
}

/**
 *
 */
inline fun AppCompatDialogFragment.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD,
    newActivity: Boolean = false
) {
    requireContext().openFragment(fragment, launchMode, newActivity)
}

/**
 * 是否需要显示底部的minbar
 */
data class ShowMiniBarEvent(val show: Boolean, val animation: Boolean = false)

inline fun SupportFragment.whetherShowMiniBar(show: Boolean = true) {
    EventBus.getDefault().post(ShowMiniBarEvent(show, true))
}
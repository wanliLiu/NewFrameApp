package com.soli.libcommon.event

import android.content.Context
import androidx.appcompat.app.AppCompatDialogFragment
import com.soli.libcommon.base.common.CommonActivity
import me.yokeyword.fragmentation.ISupportFragment
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
fun SupportFragment.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD,
    //重新开一个Activity来装载Fragment
    useEventBus: Boolean = true,
    newActivity: Boolean = false
) {
    when {
        newActivity -> CommonActivity.startFragment(requireActivity(), fragment)
        requireActivity() is CommonActivity -> start(fragment, launchMode)
        useEventBus -> EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
        else -> start(fragment, launchMode)
    }
}

/**
 *
 */
inline fun SupportFragment.popFragment() {
    EventBus.getDefault().post(OpenFragmentEvent(isPopEvent = true))
    pop()
}

/**
 *
 */
fun Context.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD,
    //重新开一个Activity来装载Fragment
    newActivity: Boolean = false
) {
    when {
        newActivity -> CommonActivity.startFragment(this, fragment)
        this is CommonActivity -> start(fragment, launchMode)
        else -> EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
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
    if (newActivity)
        CommonActivity.startFragment(requireActivity(), fragment)
    else
        EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
}

/**
 * 是否需要显示底部的minbar
 */
data class ShowMiniBarEvent(val show: Boolean)

inline fun SupportFragment.whetherShowMiniBar(show: Boolean = true) {
    EventBus.getDefault().post(ShowMiniBarEvent(show))
}
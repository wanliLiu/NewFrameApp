package com.soli.libcommon.event

import android.content.Context
import androidx.appcompat.app.AppCompatDialogFragment
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
inline fun SupportFragment.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD
) {
    EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
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
inline fun Context.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD
) {
    EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
}

/**
 *
 */
inline fun AppCompatDialogFragment.openFragment(
    fragment: SupportFragment,
    launchMode: Int = ISupportFragment.STANDARD
) {
    EventBus.getDefault().post(OpenFragmentEvent(fragment, launchMode))
}

/**
 * 是否需要显示底部的minbar
 */
data class ShowMiniBarEvent(val show: Boolean)

inline fun SupportFragment.whetherShowMiniBar(show: Boolean = true) {
    EventBus.getDefault().post(ShowMiniBarEvent(show))
}
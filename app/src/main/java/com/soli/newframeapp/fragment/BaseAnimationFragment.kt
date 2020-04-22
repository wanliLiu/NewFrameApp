package com.soli.newframeapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.soli.libcommon.R
import com.soli.libcommon.base.BaseFragment
import com.soli.libcommon.util.openFragment

/**
 *
 * @author Soli
 * @Time 2020/4/21 11:05
 */

abstract class BaseAnimationFragment : BaseFragment() {

    /**
     *
     */
    inline fun <reified T : Fragment> openFragment(
        args: Bundle? = null,
        backStack: Boolean = true,
        showAnimation: Boolean = true
    ) {
        if (activity is LaunchUIHome) {
            (activity as LaunchUIHome).apply {
                openFragment<T>(
                    R.id.id_main_container,
                    args,
                    backStack,
                    showAnimation
                )
                animationMiniBar(false)
            }
        }
    }

}
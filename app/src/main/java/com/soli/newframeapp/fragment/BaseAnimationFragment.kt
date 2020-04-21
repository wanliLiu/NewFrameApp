package com.soli.newframeapp.fragment

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
//               val tes =  supportFragmentManager.findFragmentById(R.id.id_main_container)
//                if (tes != null)
//                {
//                    supportFragmentManager.beginTransaction().hide(tes).commit()
//                }
                openFragment<T>(
                    R.id.id_main_container,
                    args,
                    backStack,
                    showAnimation
                )
                showTabBar(false)


            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation: TranslateAnimation? = null
        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            animation = if (enter) {
                TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0f
                )
            } else {
                TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    -1f,
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0f
                )
            }
        } else if (FragmentTransaction.TRANSIT_FRAGMENT_CLOSE == transit) {
            animation = if (enter) {
                TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    -1f,
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0f
                )
            } else {
                TranslateAnimation(
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    0f,
                    Animation.RELATIVE_TO_SELF,
                    0f
                )
            }
        }
        if (animation == null) {
            animation = TranslateAnimation(0f, 0f, 0f, 0f)
        }
        animation.duration = 300
        return animation
    }
}
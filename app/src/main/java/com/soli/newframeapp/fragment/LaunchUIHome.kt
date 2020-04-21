package com.soli.newframeapp.fragment

import android.animation.ValueAnimator
import android.widget.RelativeLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.soli.libcommon.util.openFragment
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.home_entrance.*

/**
 *
 * @author Soli
 * @Time 2020/4/20 14:27
 */
class LaunchUIHome : BaseLaunchUI() {

    private var showTabBar = true

    override fun getContentView() = R.layout.home_entrance

    override fun initView() {
    }


    override fun initData() {
        openFragment<HomeFragment>(R.id.id_main_container, backStack = false, showAnimation = false)
    }

    override fun initListener() {

    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (isBackToHome())
            showTabBar(true)
    }

    /**
     *
     */
    fun showTabBar(show: Boolean) {

        if (show && showTabBar) return
        if (!show && !showTabBar) return

        showTabBar = show

        ValueAnimator.ofFloat(0f, 1f).setDuration(300).apply {
            addUpdateListener {
                val tabBarHeight =
                    ctx.resources.getDimensionPixelOffset(R.dimen.home_tab_bar_height)
                val value = (tabBarHeight * it.animatedValue as Float).toInt()
                emptySpace.layoutParams.height = if (show) value else tabBarHeight - value
//                (id_main_container.layoutParams as RelativeLayout.LayoutParams).bottomMargin =
//                    if (show) tabBarHeight - value else value
                minibar.requestLayout()
            }
//            (id_main_container.layoutParams as RelativeLayout.LayoutParams).also { params ->
//                if (show) {
//                    doOnStart {
//                        params.removeRule(RelativeLayout.ABOVE)
//                        id_main_container.layoutParams = params
//                    }
//                } else
//                    doOnEnd {
//                        params.addRule(RelativeLayout.ABOVE, R.id.minibar)
//                        id_main_container.layoutParams = params
//                    }
//
//            }
            start()
        }
    }
}
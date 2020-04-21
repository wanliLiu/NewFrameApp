package com.soli.newframeapp.fragment

import android.animation.ValueAnimator
import androidx.core.animation.doOnEnd
import com.soli.libcommon.util.dip2px
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
        openFragment<HomeFragment>(R.id.id_main_container,backStack = false,showAnimation = false)
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

        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val sds = ctx.dip2px(50)
                val value = (sds * it.animatedValue as Float).toInt()
                emptySpace.layoutParams.height = if (show) value else sds - value
                minibar.requestLayout()
            }
            doOnEnd { showTabBar = show }
            start()
        }
    }
}
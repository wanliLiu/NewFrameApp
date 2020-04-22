package com.soli.newframeapp.fragment

import android.animation.ValueAnimator
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
        val frag = findFragment(HomeFragment::class.java)
        if (frag == null)
            loadRootFragment(R.id.id_main_container, HomeFragment())
    }


    override fun initData() {

    }

    override fun initListener() {

    }

    // 再点一次退出程序时间设置
    private val WAIT_TIME = 2000L
    private var TOUCH_TIME: Long = 0


    override fun onBackPressedSupport() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            if (supportFragmentManager.backStackEntryCount == 2)
                animationMiniBar(true)
            pop()
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                ActivityCompat.finishAfterTransition(this)
            } else {
                TOUCH_TIME = System.currentTimeMillis()
                Toast.makeText(ctx, "再按一次退出程序！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *
     */
    fun animationMiniBar(show: Boolean) {

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
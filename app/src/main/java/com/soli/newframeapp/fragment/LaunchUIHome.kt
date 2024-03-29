package com.soli.newframeapp.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.soli.libcommon.base.BaseSwipeBackFragment
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.HomeEntranceBinding
import com.soli.newframeapp.event.OpenFragmentEvent
import com.soli.newframeapp.event.ShowMiniBarEvent
import me.yokeyword.fragmentation.ISupportFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @author Soli
 * @Time 2020/4/20 14:27
 */
class LaunchUIHome : BaseLaunchUI<HomeEntranceBinding>() {

    // 再点一次退出程序时间设置
    private val WAIT_TIME = 2000L
    private var TOUCH_TIME: Long = 0

    private var showTabBar = true
    private var isFirstSwipe = false

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.apply {
            showTabBar = getBoolean("showTabBar", showTabBar)
            isFirstSwipe = getBoolean("isFirstSwipe", isFirstSwipe)
        }
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        val frag = findFragment(HomeFragment::class.java)
        if (frag == null)
            loadRootFragment(R.id.id_main_container, HomeFragment())
    }


    override fun initData() = Unit

    override fun initListener() = Unit

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isFirstSwipe", isFirstSwipe)
        outState.putBoolean("showTabBar", showTabBar)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.apply {
            showTabBar = getBoolean("showTabBar", showTabBar)
            isFirstSwipe = getBoolean("isFirstSwipe", isFirstSwipe)
        }
    }

    override fun onBackPressedSupport() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            if (supportFragmentManager.backStackEntryCount == 2)
                animationMiniBar()
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
     * 所有打开Fragment地方都是通过这个来打开，通过发送事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openNewFragmentEvent(event: OpenFragmentEvent) {
        if (event.isPopEvent) {
            animationMiniBar()
        } else {
            start(event.fragment, event.launchMode)
            whenOpenNewTakeSomething(event.fragment)
        }
    }

    /**
     *
     */
    private fun whenOpenNewTakeSomething(toFragment: ISupportFragment?) {
        animationMiniBar(false)
        if (!isFirstSwipe && toFragment is BaseSwipeBackFragment<*>) {
            if (toFragment.needSwipeBack()) {
                isFirstSwipe = true
                toFragment.dragStateCallBack = { done, progress ->
                    isFirstSwipe = !done
                    swipeAnimationMiniBar(progress)
                }
            }
        }
    }

    /**
     * 滑动退出的minibar的动画
     */
    private fun swipeAnimationMiniBar(proress: Float) {

        if (showTabBar || binding.minibar.isInvisible) return

        if (proress >= 1f)
            showTabBar = true

        if (proress <= 0f)
            showTabBar = false


        val tabBarHeight =
            ctx.resources.getDimensionPixelOffset(R.dimen.home_tab_bar_height)
        val value = (tabBarHeight * proress).toInt()
        binding.emptySpace.layoutParams.height = value
        binding.minibar.requestLayout()
    }

    /**
     *
     */
    private fun animationMiniBar(show: Boolean = true) {

        if ((show && showTabBar) || (!show && !showTabBar)) return

        if (show) {
            takeActionToMiniBar(show)
        }

        showTabBar = show

        isFirstSwipe = false

        ValueAnimator.ofFloat(0f, 1f).setDuration(300).apply {
            addUpdateListener {
                val tabBarHeight =
                    ctx.resources.getDimensionPixelOffset(R.dimen.home_tab_bar_height)
                val value = (tabBarHeight * it.animatedValue as Float).toInt()
                binding.emptySpace.layoutParams.height = if (show) value else tabBarHeight - value
                binding.minibar.requestLayout()
            }
            start()
        }
    }

    /**
     * 有些界面不需要显示minibar
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun whetherShowMiniBarEvent(event: ShowMiniBarEvent) {
        takeActionToMiniBar(event.show)
    }

    /**
     *
     */
    private fun takeActionToMiniBar(show: Boolean) {
        if ((show && binding.minibar.isVisible) || (!show && !binding.minibar.isVisible)) return

        binding.minibar.visibility = View.VISIBLE
        binding.minibar.alpha = if (show) 0.0f else 1.0f

        binding.minibar.animate().alpha(1.0f - binding.minibar.alpha)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (!show) binding.minibar.visibility = View.GONE
                }
            }).start()
    }
}
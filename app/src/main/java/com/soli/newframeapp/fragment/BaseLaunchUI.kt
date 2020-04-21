package com.soli.newframeapp.fragment

import android.view.KeyEvent
import android.widget.Toast
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.StatusBarUtil
import com.soli.newframeapp.R
import java.util.*

/**
 * @author Soli
 * @Time 2020/4/20 13:55
 */
abstract class BaseLaunchUI : BaseActivity() {

    private var pressTime = 0

    override fun needTopToolbar() = false

    override fun needSliderActivity() = false

    override fun dealCusotomStatus(color: Int, statusBarAlpha: Int): Boolean {
        StatusBarUtil.setTransparentForWindow(this)
        return true
    }

//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount != 0)
//            supportFragmentManager.popBackStackImmediate()
//        else
//            super.onBackPressed()
//    }


    /**
     * 是否返回到主页面
     */
    fun isBackToHome(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.id_main_container)
        return fragment == null //&& (fragment is HomeFragment)
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (isBackToHome()) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                when (pressTime++) {
                    0 -> {
                        Toast.makeText(ctx, "再按一次退出程序！", Toast.LENGTH_SHORT).show()
                        val timer = Timer()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                pressTime = 0
                            }
                        }, 3000)
                        return true
                    }
                    1 -> {
                        onBackPressed()
                        return true
                    }
                    else -> {
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
package com.soli.newframeapp.main

import androidx.core.app.ActivityCompat
import com.soli.libcommon.base.BaseMultiFragmentActivity
import com.soli.libcommon.util.ToastUtils
import com.soli.newframeapp.R

class MainActivity : BaseMultiFragmentActivity() {

    // 再点一次退出程序时间设置
    private val WAIT_TIME = 2000L
    private var TOUCH_TIME: Long = 0

    override fun needSliderActivity() = false

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        val homeFragment = findFragment(MainFragment::class.java)
        if (homeFragment == null) {
            loadRootFragment(R.id.home_container, MainFragment())
        }
    }

    override fun initListener() = Unit
    override fun initData() = Unit


    override fun onBackPressedSupport() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            pop()
        } else {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                ActivityCompat.finishAfterTransition(this)
            } else {
                TOUCH_TIME = System.currentTimeMillis()
                ToastUtils.showShortToast("再按一次退出程序！")
            }
        }
    }
}

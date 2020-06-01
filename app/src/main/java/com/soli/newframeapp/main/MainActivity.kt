package com.soli.newframeapp.main

import com.soli.libcommon.base.BaseMultiFragmentActivity
import com.soli.newframeapp.R

class MainActivity : BaseMultiFragmentActivity() {

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

}

package com.soli.newframeapp

import android.content.Intent
import com.soli.lib_common.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun needShowBackIcon() = false

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        setTitle("New Frame")
    }

    override fun initListener() {
        java.setOnClickListener { startActivity(Intent(ctx,SecondAcitivity::class.java)) }
    }

    override fun initData() {
    }
}

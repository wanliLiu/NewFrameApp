package com.soli.newframeapp

import android.content.Intent
import android.os.Handler
import android.view.View
import com.soli.lib_common.base.BaseActivity
import com.soli.lib_common.util.NetworkUtil
import com.soli.lib_common.view.root.LoadingType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private val retryIndex: Int = 1
    private var retry: Int = 0

    override fun needShowBackIcon() = false

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        title = "New Frame"

    }

    override fun initListener() {

        fragmentTest.setOnClickListener(this)
        LauchActivity.setOnClickListener(this)

        progressInTest.setOnClickListener {
            showProgress()
            Handler().postDelayed({
                dismissProgress()
            }, 2000)
        }

        progressDialogTest.setOnClickListener {
            showProgress(LoadingType.TypeDialog)
            Handler().postDelayed({
                dismissProgress()
            }, 2000)
        }

        loaddingErroTest.setOnClickListener {
            retry = 0
            loadingErrorTest()
        }
    }

    override fun initData() {
        NetworkUtil.isAvailableByPing()
    }

    private fun loadingErrorTest() {
        showProgress()
        Handler().postDelayed({
            dismissProgress()
            if (retry < retryIndex)
                errorHappen {
                    retry++
                    loadingErrorTest()
                }
        }, 2000)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.LauchActivity -> startActivity(Intent(ctx, SecondAcitivity::class.java))
            R.id.fragmentTest -> startActivity(Intent(ctx, FragmentTestActivity::class.java))
        }
    }
}

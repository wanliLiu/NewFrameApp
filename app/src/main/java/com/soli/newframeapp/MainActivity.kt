package com.soli.newframeapp

import android.content.Intent
import android.os.Handler
import android.view.View
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.ApiParams
import com.soli.libCommon.util.NetworkUtil
import com.soli.libCommon.util.ToastUtils
import com.soli.libCommon.view.root.LoadingType
import com.soli.newframeapp.net.NetWorkTestActivity
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
        netWorkTest.setOnClickListener(this)
        showStartnetWorkTest.setOnClickListener(this)
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
            R.id.netWorkTest -> startActivity(Intent(ctx, NetWorkTestActivity::class.java))
            R.id.showStartnetWorkTest -> showStartEventPost()
        }
    }

    /**
     * 利用秀动的网络,刚好测试一下问题
     */
    private fun showStartEventPost() {
        val params = ApiParams()

        params.put("title", "哦www用")
        params.put("remark", "急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民急急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急急竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急毕竟估")
        params.put("startTime", "1528178400662")
        params.put("endTime", "1528182000662")
        params.put("repeatRule", "")
        params.put("remind", "0")//提醒

        ApiHelper.Builder()
                .url("event/addOrUpdate.json")
                .params(params)
                .build()
                .post { result ->

                    if (result.isSuccess)
                        ToastUtils.showShortToast("添加成功")
                    else
                        ToastUtils.showShortToast(".......,失败了哦")
                }
    }
}

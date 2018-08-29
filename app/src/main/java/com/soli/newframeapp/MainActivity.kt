package com.soli.newframeapp

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.view.View
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.ApiParams
import com.soli.libCommon.util.NetworkUtil
import com.soli.libCommon.util.ToastUtils
import com.soli.libCommon.view.root.LoadingType
import com.soli.newframeapp.download.DownloadTestActivity
import com.soli.newframeapp.net.NetWorkTestActivity
import com.soli.newframeapp.net.WebviewActivity
import com.soli.permissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private val retryIndex: Int = 1
    private var retry: Int = 0
    private val rxPermissions by lazy { RxPermissions(ctx) }


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
        fileDownload.setOnClickListener(this)
        webViewTest.setOnClickListener(this)
        _23Test.setOnClickListener(this)

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
            R.id.fileDownload -> checkPermission()
            R.id.webViewTest -> startActivity(Intent(ctx, WebviewActivity::class.java))
            R.id._23Test -> startActivity(Intent(ctx, Android7Activity::class.java))
        }
    }

    /**
     *
     */
    private fun checkPermission() {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { pass ->
                    if (pass)
                        startActivity(Intent(ctx, DownloadTestActivity::class.java))
                    else {
                        ToastUtils.showShortToast("需要文件读写权限")
                    }
                }
    }

    /**
     * 利用秀动的网络,刚好测试一下问题
     */
    private fun showStartEventPost() {
        val params = ApiParams()

        params.apply {
            put("title", "哦www用")
            put("remark", "急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民急急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急急竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急急急毕竟急急急毕竟估计民进急急急急急毕竟估计民进急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急民进急急急急急毕竟估计民进急急急急急毕竟计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急急急急毕竟估计民进急急毕竟估计民进急急急急急毕竟估")
            put("startTime", "1528178400662")
            put("endTime", "1528182000662")
            put("repeatRule", "")
            put("remind", "0")//提醒
        }

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

package com.soli.newframeapp

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.os.Handler
import android.view.View
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.ApiParams
import com.soli.libCommon.net.ApiResult
import com.soli.libCommon.util.FileUtil
import com.soli.libCommon.util.NetworkUtil
import com.soli.libCommon.util.ToastUtils
import com.soli.libCommon.view.root.LoadingType
import com.soli.newframeapp.net.NetWorkTestActivity
import com.soli.newframeapp.net.WebviewActivity
import com.soli.permissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity(), View.OnClickListener {

    private val retryIndex: Int = 1
    private var retry: Int = 0
    private val rxPermissions by lazy { RxPermissions(ctx) }
    private val dialog by lazy {
        val dialog = ProgressDialog(ctx)
        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        dialog.setTitle("下载")
        dialog.setMessage("正在下载，请稍后...")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setCancelable(false)
        dialog
    }

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
            R.id._23Test -> startActivity(Intent(ctx,Android7Activity::class.java))
        }
    }

    /**
     *
     */
    private fun checkPermission() {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe { pass ->
                    if (pass)
                        fileDownload()
                    else {
                        ToastUtils.showShortToast("需要文件读写权限")
                    }
                }
    }


    /**
     * 网络文件下载测试
     */
    private fun fileDownload() {

        dialog.show()
        dialog.progress = 0

        ApiHelper.Builder()
                .fileUrl("http://wxz.myapp.com/16891/1A7676B8DB1A463710B911EC8523FAB8.apk?fsname=com.showstartfans.activity_4.1.1_20180326.apk&hsr=4d5s")
                .saveFile(FileUtil.getFile(ctx, "download", "showstart.apk", false))
                .build()
                .downloadFile({ result: ApiResult<File>? ->
                    dialog.dismiss()
                    if (result!!.isSuccess)
                        if (result.result.exists())
                            ToastUtils.showLongToast("文件下载成功！：${result.fullData}")
                        else
                            ToastUtils.showShortToast(result.errormsg)
                }, { _, bytesRead, fileSize, _ ->
                    dialog.max = (fileSize / 1024).toInt()
                    dialog.progress = (bytesRead / 1024).toInt()
                })
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

package com.soli.newframeapp.download

import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.view.View
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.ApiResult
import com.soli.libCommon.util.FileUtil
import com.soli.libCommon.util.ToastUtils
import com.soli.newframeapp.R
import com.soli.newframeapp.util.InstallUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_download_test.*
import java.io.File
import java.util.concurrent.TimeUnit


class DownloadTestActivity : BaseActivity() {

    private val downloadPath = "http://wxz.myapp.com/16891/8F4C11ED51021765F70085CB5B2C2413.apk?fsname=com.showstartfans.activity_4.2.0_20180831.apk&hsr=4d5s"
    private val savePath = FileUtil.getFile(ctx, "download", "showstart_4.2.0.apk", false)

    private var mDisposable: Disposable? = null//可以取消观察者

    private val dialog by lazy {
        val dialog = ProgressDialog(ctx)
        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        dialog.setTitle("下载")
        dialog.setMessage("正在下载，请稍后...")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setCancelable(false)
        dialog
    }

    private var downloadBinder: DownloadService.DownloadBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            downloadBinder = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder = service as DownloadService.DownloadBinder

            val downloadId = downloadBinder?.startDownload(downloadPath, savePath)
            startCheckProgress(downloadId!!)
        }

    }


    override fun getContentView() = R.layout.activity_download_test

    override fun initView() {
        title = "下载测试"
    }

    override fun initListener() {

        customDownload.setOnClickListener { fileDownload() }

        systDownload.setOnClickListener {
            val intent = Intent(ctx, DownloadService::class.java)
            ContextCompat.startForegroundService(ctx, intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        webViewDownload.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(downloadPath)
            startActivity(intent)
        }
    }

    override fun initData() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            systDownload.visibility = View.GONE
    }

    /**
     * 网络文件下载测试
     */
    private fun fileDownload() {

        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        dialog.show()
        dialog.progress = 0

        ApiHelper.Builder()
                .fileUrl(downloadPath)
                .saveFile(savePath)
                .build()
                .downloadFile({ result: ApiResult<File>? ->
                    dialog.dismiss()
                    if (result!!.isSuccess)
                        if (result.result.exists()) {
                            ToastUtils.showLongToast("文件下载成功！：${result.fullData}")
                            InstallUtil.install(ctx, result.result)
                        } else
                            ToastUtils.showShortToast(result.errormsg)
                }, { _, bytesRead, fileSize, _ ->
                    dialog.max = (fileSize / 1024).toInt()
                    dialog.progress = (bytesRead / 1024).toInt()
                })
    }

    //开始监听进度
    private fun startCheckProgress(downloadId: Long) {
        mDisposable = Observable.interval(10, 20, TimeUnit.MILLISECONDS, Schedulers.io())//无限轮询,准备查询进度,在io线程执行
                .filter { downloadBinder != null }
                .map { downloadBinder?.getProgress(downloadId) }//获得下载进度
                .takeUntil { it >= 100 }//返回true就停止了,当进度>=100就是下载完成了
                .distinct()//去重复
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    dialog.setProgressNumberFormat("%1d/%2d")
                    dialog.show()
                    dialog.progress = 0
                    dialog.max = 100
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dialog.progress = it!!
                }, {
                    it.printStackTrace()
                    ToastUtils.showShortToast("出错")
                }, {
                    dialog.dismiss()
                    ToastUtils.showShortToast("下载完成")
                })
    }

    override fun onDestroy() {
        mDisposable?.dispose()
        downloadBinder?.stopEverthing()
        super.onDestroy()
    }
}

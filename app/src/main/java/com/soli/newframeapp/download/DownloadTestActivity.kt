package com.soli.newframeapp.download

import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.View
import androidx.core.content.ContextCompat
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.net.download.FileProgressListener
import com.soli.libcommon.util.FileUtil
import com.soli.libcommon.util.MLog
import com.soli.libcommon.util.ToastUtils
import com.soli.newframeapp.databinding.ActivityDownloadTestBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class DownloadTestActivity : BaseActivity<ActivityDownloadTestBinding>() {

    private val downloadPath =
        "http://img02-xusong.taihe.com/100016_2744ef0477aacf3360de229a61ae4c0c_[720_1280_4865].mp4"
    private val savePath by lazy {
//        FileUtil.getFile(ctx, "download", "showstart_4.4.3.apk", false)
        FileUtil.getFile(ctx, "download", "100016_2744ef0477aacf3360de229a61ae4c0c_.4.3.mp4", false)
    }

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

    override fun initView() {
        title = "下载测试"
    }

    override fun initListener() {

        binding.customDownload.setOnClickListener { fileDownload() }

        binding.systDownload.setOnClickListener {
            val intent = Intent(ctx, DownloadService::class.java)
            ContextCompat.startForegroundService(ctx, intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.webViewDownload.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(downloadPath)
            startActivity(intent)
        }
    }

    override fun initData() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) binding.systDownload.visibility =
            View.GONE
    }

    /**
     * 网络文件下载测试
     */
    private fun fileDownload() {

        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        dialog.show()
        dialog.progress = 0


        ApiHelper.build {
            fileUrl = downloadPath
            saveFile = savePath
        }.downloadFile({ result ->
            dialog.dismiss()
            if (result.isSuccess && result.result != null) if (result.result!!.exists()) {
                MLog.d("fileDownload", result.fullData)
                ToastUtils.showLongToast("文件下载成功！：${result.fullData}")
//                InstallUtil.install(ctx, result.result!!)
            } else ToastUtils.showShortToast(result.errormsg)
        }, object : FileProgressListener {
            override fun progress(
                progress: Int, bytesRead: Long, updateBytes: Long, fileSize: Long, isDone: Boolean
            ) {
                dialog.max = (fileSize / 1024).toInt()
                dialog.progress = (bytesRead / 1024).toInt()
            }

        })
    }

    //开始监听进度
    private fun startCheckProgress(downloadId: Long) {
        mDisposable =
            Observable.interval(10, 20, TimeUnit.MILLISECONDS, Schedulers.io())//无限轮询,准备查询进度,在io线程执行
                .filter { downloadBinder != null }
                .map { downloadBinder?.getProgress(downloadId)!! }//获得下载进度
                .takeUntil { it >= 100 }//返回true就停止了,当进度>=100就是下载完成了
                .distinct()//去重复
                .subscribeOn(Schedulers.io()).doOnSubscribe {
                    dialog.setProgressNumberFormat("%1d/%2d")
                    dialog.show()
                    dialog.progress = 0
                    dialog.max = 100
                }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
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

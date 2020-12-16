package com.soli.newframeapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.soli.libcommon.util.ShellUtils
import com.soli.libcommon.util.ToastUtils
import com.soli.newframeapp.BuildConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File


/**
 * 安装apk文件
 * @author Soli
 * @Time 2018/8/29 13:58
 */
object InstallUtil {

    /**
     * 安装
     * @param ctx
     * @param apkPath 要安装的APK
     * @param rootMode 是否是Root模式 默认为非root模式安装
     */
    fun install(ctx: Context, apkPath: File, rootMode: Boolean = false) {
        if (rootMode) {
            installRoot(ctx, apkPath)
        } else {
            installNormal(ctx, apkPath)
        }
    }

    /**
     * 普通安装
     */
    private fun installNormal(ctx: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        intent.setDataAndType(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(ctx, "${BuildConfig.APPLICATION_ID}.fileProvider", file)
        } else {
            Uri.fromFile(file)
        }, "application/vnd.android.package-archive")

        ctx.startActivity(intent)
    }

    /**
     * 通过Root方式安装
     */
    private fun installRoot(context: Context, apkPath: File) {
      val dis =  Observable.just(apkPath)
                .map { "pm install -r $it" }
                .map { ShellUtils.execCmd(it, true) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    if (result.result == 0) {
                        ToastUtils.showLongToast("安装成功")
                    } else {
                        ToastUtils.showShortToast("root权限获取失败,尝试普通安装")
                        install(context, apkPath)
                    }
                }
    }
}
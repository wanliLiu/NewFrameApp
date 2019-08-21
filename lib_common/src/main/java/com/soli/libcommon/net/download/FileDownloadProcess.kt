package com.soli.libcommon.net.download

import com.soli.libcommon.base.Constant
import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.util.FileUtil
import com.soli.libcommon.util.MLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 *
 * @author Soli
 * @Time 2018/12/21 15:11
 */
class FileDownloadProcess(
    private val downloadList: List<String>, //下载的文件链接
    private val saveInInnerStorage: Boolean = true, //是否存在android.data.目录下  默认存储
    private val updateInfo: ((fileInfo: DownloadInfo) -> Unit)? = null, // 多个文件下载的时候，每下载完一个回调一下
    private val downloadProgress: ((progress: Int) -> Unit)? = null, //每个文件的下载进度
    private val callback: ((isSuccess: Boolean, fileInfo: ArrayList<DownloadInfo>?) -> Unit)? = null  //所有下载完后统一回调
) {

    data class DownloadInfo(
        val url: String,//文件原始地址
        val filePath: String//文件本地地址
    )

    private var downIndex = 0
    private val info: ArrayList<DownloadInfo> = ArrayList()

    /**
     *
     */
    private fun downloadFile(url: String): Observable<String> {
        return Observable.create { source ->
            if (!source.isDisposed) {

                val savePath = FileUtil.getDownLoadFilePath(Constant.getContext(), url, saveInInnerStorage)

                if (savePath.exists()) {
                    //要下载的文件如果存在就直接返回
                    source.onNext(savePath.absolutePath)
                } else {
                    ApiHelper.Builder()
                        .fileUrl(url)
                        .saveFile(savePath)
                        .build()
                        .downloadFile({ result ->
                            if (result.isSuccess) {
                                source.onNext(result.result.absolutePath)
                            } else
                                source.onError(IllegalArgumentException(result.errormsg))
                        }, { progress, _, _, _, _ ->
                            downloadProgress?.invoke(progress)
                            MLog.d("文件下载：$progress")
                        })
                }
            }
        }
    }

    private fun dealDownload(url: String): Disposable {
        return Observable.just(url)
            .flatMap { downloadFile(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                MLog.e("文件下载", it.message)
                callback?.invoke(false, info)
            }
            .onErrorResumeNext(Observable.empty())
            .subscribe {

                val data = DownloadInfo(url, it)
                updateInfo?.invoke(data)

                info.add(data)

                downIndex++
                if (downIndex >= downloadList.size) {
                    callback?.invoke(true, info)
                } else {
                    try {
                        dealDownload(downloadList[downIndex])
                    } catch (e: Exception) {
                        //just in case
                        e.printStackTrace()
                        callback?.invoke(false, info)
                    }
                }
            }
    }

    /**
     * 开始下载
     */
    fun startDownload() {
        downIndex = 0
        info.clear()

        dealDownload(downloadList[downIndex])
    }
}

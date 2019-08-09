package com.soli.libcommon.net.upload

import android.app.ProgressDialog
import android.content.Context
import com.alibaba.fastjson.JSON
import com.soli.libcommon.base.Constant
import com.soli.libcommon.net.ApiCallBack
import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.net.download.FileProgressListener
import com.soli.libcommon.util.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 *  图片上传统一工具
 * @author Soli
 * @Time 2018/12/6 13:25
 */
class FileUploadProcess(
    private val ctx: Context,
    private val uploadFileList: ArrayList<String>,
    private val needZip: Boolean = true, //是否需要压缩  默认需要
    private val needOrignt: Boolean = false,//是否需要原图，默认不需要
    private val needShowProgressLoading: Boolean = false,//是否需要显示上传进度Dialog
    private val uploadProgress: ((progress: Int) -> Unit)? = null,//上传进度回调
    private val updateInfo: ((uploadInfo: FileUploadInfo?) -> Unit)? = null,//多张上传的时候，每上传一个，就回调信息
    private val callBack: ((isSuccess: Boolean, uploadInfo: ArrayList<FileUploadInfo>?) -> Unit)? = null
) {


    data class UploadInfo(
        val isDone: Boolean,//是否完成
        val bytesUpdate: Long, //实时上传的进度更新
        val originPath: String, //本地文件地址
        val path: String,//上传成功后的path
        val width: Int,
        val height: Int
    ) {

        constructor(origin: String, mUpdates: Long) : this(false, mUpdates, origin, "", 0, 0)

        constructor(origin: String, mpath: String, mWidth: Int, mHeight: Int) : this(
            true,
            0,
            origin,
            mpath,
            mWidth,
            mHeight
        )
    }

    //上传文件的总大小
    private var uploadFileSize = 0L
    //上传的进度
    private var bytesSend = 0L
    private var haveUpload = 0L

    private val info: ArrayList<FileUploadInfo> = ArrayList()
    private val afterDealList = ArrayList<String>()

    private val dialog by lazy {
        val dialog = ProgressDialog(ctx)
//        dialog.setProgressNumberFormat("%1d KB/%2d KB")
//        dialog.setTitle("上传")
        dialog.max = 100
        dialog.setMessage("图片处理中.......")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setCancelable(false)
        dialog
    }

    private var optionIndex = 0
    private var progress = 0

    /**
     * 处理图片是否需要压缩
     */
    private fun dealIfZipImage(file: File): Observable<String> {

        return Observable.create { source ->
            if (!source.isDisposed) {
                if (needZip && FileUtil.isImageFile(fullPath = file.absolutePath)) {
                    ImageDeal.compressPic(
                        Constant.getContext(),
                        if (needOrignt) Luban.FIRST_GEAR else Luban.THIRD_GEAR,
                        file.absolutePath
                    ) { source.onNext(it) }
                } else
                    source.onNext(file.absolutePath)
            }
        }
    }

    private class fileUploadProgress(private val source: ObservableEmitter<UploadInfo>, private val path: String) :
        FileProgressListener {
        override fun progress(progress: Int, totalBytesSend: Long, updateBytes: Long, fileSize: Long, isDone: Boolean) {
            source.onNext(UploadInfo(path, totalBytesSend))
        }
    }

    private class fileUploadCallBack(private val source: ObservableEmitter<UploadInfo>, private val path: String) :
        ApiCallBack<String> {
        override fun receive(result: ApiResult<String>) {
            if (result.isSuccess) {
                try {
                    val json = JSON.parseObject(result.fullData)
                    val dataJson = json.getJSONObject("result")
                    var width = 0
                    var height = 0
                    if (FileUtil.isImageFile(fullPath = path)) {
                        val opt = ImageDeal.getImageOpt(File(path))
                        if (opt != null) {
                            width = opt.outWidth
                            height = opt.outHeight
                        }
                    } else if (FileUtil.isVideoFile(fullPath = path)) {
                        try {
                            FileUtil.getVideoFileWidthAndHeight(path).apply {
                                width = this[0].toFloat().toInt()
                                height = this[1].toFloat().toInt()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    source.onNext(UploadInfo(path, dataJson["path"].toString(), width, height))
                } catch (e: Exception) {
                    e.printStackTrace()
                    source.onError(e)
                }
            } else {
                source.onError(IllegalArgumentException("${result.errorCode}------${result.errormsg}"))
            }
        }
    }

    /**
     * 具体图片上传
     */
    private fun dealFileUpload(path: String): Observable<UploadInfo> {
        return Observable.create { source ->
            if (!source.isDisposed) {
                ApiHelper.Builder()
                    .fileUrl(path)
                    //todo 根据实际情况来弄
//                    .baseUrl(Constant.requestFileUploadHost)
//                    .url(Constant.newFileUploadAction)
                    .build()
                    .uploadFileNew(fileUploadCallBack(source, path), fileUploadProgress(source, path))
            }
        }
    }

    private fun dealImage(path: String, callback: (newPath: String) -> Unit): Disposable {
        return Observable.just(path)
            .map { path -> File(path) }
            .filter { it.exists() }
            .flatMap { dealIfZipImage(it) }
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe { callback.invoke(it) }
    }

    /**
     * 上传图片，统一压缩一下
     */
    private fun dealImageBeforeUpload(callback: () -> Unit) {

        if (optionIndex >= uploadFileList.size)
            callback.invoke()

        dealImage(uploadFileList[optionIndex]) {
            uploadFileSize += FileSizeUtil.getFileSize(File(it))
            afterDealList.add(it)

            optionIndex++
            if (optionIndex >= uploadFileList.size) {
                MLog.e("文件上传", "上传文件的总大小${FileSizeUtil.FormetFileSize(uploadFileSize)}")
                callback.invoke()
            } else
                dealImageBeforeUpload(callback)
        }
    }

    /**
     * 具体处理图片上传操作
     */
    private fun dealUpload(file: String): Disposable {
        return Observable.just(file)
            .map { path -> File(path) }
            .filter { it.exists() }
            .flatMap { dealFileUpload(it.absolutePath) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                MLog.e("文件上传", it.message)
                if (needShowProgressLoading)
                    dialog.dismiss()
                callBack?.invoke(false, info)
            }
            .onErrorResumeNext(Observable.empty())
            .subscribe {
                if (it.isDone) {
                    haveUpload += FileSizeUtil.getFileSize(File(it.originPath))

                    val data = FileUploadInfo(it.originPath, it.path, it.width, it.height)

                    updateInfo?.invoke(data)

                    info.add(data)

                    optionIndex++
                    if (optionIndex >= afterDealList.size) {
                        if (needShowProgressLoading)
                            dialog.dismiss()
                        callBack?.invoke(true, info)
                    } else {
                        try {
                            dealUpload(afterDealList[optionIndex])
                        } catch (e: Exception) {
                            e.printStackTrace()
                            //just in case
                            if (needShowProgressLoading)
                                dialog.dismiss()
                            callBack?.invoke(false, info)
                        }
                    }
                } else {
                    bytesSend = haveUpload + it.bytesUpdate
                    val temp = ((100 * bytesSend) / uploadFileSize).toInt()
                    if (progress != temp) {
                        progress = temp
                        uploadProgress?.invoke(progress)
                        if (needShowProgressLoading) {
                            dialog.progress = progress
                        }
                        MLog.e("文件上传", "上传进度$progress")
                    }
                }
            }
    }

    fun startUpload() {

        optionIndex = 0

        afterDealList.clear()
        uploadFileSize = 0

        info.clear()

        if (needShowProgressLoading) {
            dialog.show()
            dialog.progress = 0
        }

        dealImageBeforeUpload {

            optionIndex = 0

            progress = 0
            haveUpload = 0L
            bytesSend = 0L

            if (needShowProgressLoading) {
                dialog.setMessage("图片处理完成，上传进行中........")
            }
            dealUpload(afterDealList[optionIndex])
        }
    }

}
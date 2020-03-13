package com.soli.newframeapp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.download.FileDownloadProcess
import com.soli.libcommon.util.FileUtil
import com.soli.libcommon.util.ImageLoader
import com.soli.libcommon.util.ToastUtils
import com.soli.libcommon.view.root.LoadingType
import com.soli.permissions.RxPermissions
import kotlinx.android.synthetic.main.activity_android7.*
import java.io.File


/**
 *  Android 相关文件适配功能测试
 * @author Soli
 * @Time 2018/8/29 09:47
 */
class Android7Activity : BaseActivity() {

    private val requestSelectFileCode = 32
    private val requestOpenCamera = 33

    private var imagePath: File? = null
    private var cameraUri: Uri? = null

    private val rxPermissions by lazy { RxPermissions(ctx) }

    override fun getContentView() = R.layout.activity_android7

    override fun initView() {
        title = "Android >=23 Android Q测试"
    }

    override fun initListener() {
        camerText.setOnClickListener { checkPermission() }

        openDocumentPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "*/*"
//            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(intent, requestSelectFileCode)
        }

        tartgetQ.setOnClickListener {
            downloadPicAndSaveInPublicStorage()
        }
    }

    override fun initData() {
    }

    /**
     *
     */
    private fun downloadPicAndSaveInPublicStorage() {

        val downlist = arrayListOf(
            "https://s2.showstart.com/img/2020/20200309/11b861102cb049b487b5f2b99ad011e2_1200_400_101015.0x0.jpg?imageMogr2/thumbnail/!1200x400r/gravity/Center/crop/!1200x400",
            "https://s2.showstart.com/img/2019/20191008/bda32d33f5f44168b96742b2fb29a8c3_600_800_733494.0x0.jpg",
            "https://s2.showstart.com/img/2019/20190826/9f10aa7b205d48ec853413f6cdff3d48_600_800_122780.0x0.jpg",
            "http://img02-xusong.taihe.com/100016_2744ef0477aacf3360de229a61ae4c0c_[720_1280_4865].mp4",
            "http://img02-xusong.taihe.com/3717fe12434c0f8e1f76571cddc49588_0_0.mp3",
            "https://s2.showstart.com/img/2019/20190809/a2389dcbeb8d42f492c458c4a59b4d3b_600_800_92235.0x0.jpg?",
            "http://img02-xusong.taihe.com/8df5a431b99e217ca757d3cddc7b700b_0_0.mp3",
            "https://s2.showstart.com/img/2018/20181226/06b037a1383146a89eb7d1537bc86841_1280_900_2350842.0x0.jpg"
        )

        showProgress(type = LoadingType.TypeDialog, cancle = false)
        loadingDialg()?.showNumProgress()

        FileDownloadProcess(downlist,
            downloadProgress = { progress -> loadingDialg()?.setProgress(progress) },
            customSavePath = { url, origin ->
                if (!FileUtil.isAndroidQorAbove)
                    File(FileUtil.getUserCanSeeDir(ctx), FileUtil.getFileName("joker_", url))
                else
                    origin
            })
        { isSuccess, fileInfo ->
            dismissProgress()
            if (isSuccess) {
                fileInfo?.forEach { info ->
                    if (FileUtil.isAndroidQorAbove)
                        FileUtil.storeFileInPublicAtTargetQ(
                            ctx,
                            File(info.filePath)
                        )
                    else
                        FileUtil.scanMediaForFile(ctx, info.filePath)
                }
                ToastUtils.showShortToast("下载成功")
            } else {
                ToastUtils.showShortToast("下载失败")
            }
        }.startDownload()
    }

    /**
     *
     */
    private fun checkPermission() {
        val temp = rxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .subscribe { pass ->
                if (pass)
                    takePicture()
                else {
                    ToastUtils.showShortToast("需要相应的权限")
                }
            }
    }

    /**
     * Android 调用相机拍照，适配到Android 10
     * https://juejin.im/post/5d80edb76fb9a06b1c746176
     *
     * Android 10 加载手机本地图片
     * https://juejin.im/post/5d80ef726fb9a06aeb10f223
     */
    private fun takePicture() {
        imagePath =
            File(FileUtil.getUserCanSeeDir(ctx), "${System.currentTimeMillis()}_picture.jpeg")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //拍照结果输出到这个uri对应的file中
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT, when {
                FileUtil.isAndroidQorAbove -> {
                    cameraUri =
                        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
                            contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                ContentValues().apply {
                                    put(
                                        MediaStore.Video.Media.RELATIVE_PATH,
                                        FileUtil.UserMediaPath
                                    )
                                }
                            )
                        else
                            contentResolver.insert(
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                                ContentValues().apply {
                                    put(
                                        MediaStore.Video.Media.RELATIVE_PATH,
                                        FileUtil.UserMediaPath
                                    )
                                }
                            )

                    cameraUri
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    //对这个uri进行授权
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    FileProvider.getUriForFile(
                        ctx,
                        "${BuildConfig.APPLICATION_ID}.fileProvider",
                        imagePath!!
                    )
                }
                else -> Uri.fromFile(imagePath)
            }
        )
        // 打开Camera
        startActivityForResult(intent, requestOpenCamera)
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {

        var res: String? = null
        val proj = arrayListOf(MediaStore.Images.Media.DATA)
        if (FileUtil.isAndroidQorAbove)
            proj.add(MediaStore.Images.Media.RELATIVE_PATH)
        val cursor = contentResolver.query(
            contentUri,
            proj.toTypedArray(), null, null, null
        )
        if (null != cursor && cursor.moveToFirst()) {
            res = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            Log.e("path-belowQ", res)
            if (FileUtil.isAndroidQorAbove) {
                val relative_path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
                Log.e("path--Q", relative_path)
            }
            cursor.close()
        }


        return res
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestOpenCamera -> {
                    if (FileUtil.isAndroidQorAbove) {
                        Log.e("imageUrl", cameraUri?.toString() ?: "Android 10上图片")
                        pickImageFresco.setImageURI(cameraUri, ctx)
                        pickImage.setImageURI(cameraUri)
                        copyFileToPrivateArea(cameraUri)
                        ToastUtils.showShortToast(cameraUri?.toString() ?: "Android 10上图片")
                    } else if (imagePath!!.exists()) {
                        ToastUtils.showShortToast("${imagePath!!.absolutePath} 文件存在")
                        pickImage.setImageBitmap(BitmapFactory.decodeFile(imagePath!!.absolutePath))
                        ImageLoader.loadImageByPath(pickImageFresco, imagePath!!.absolutePath)
                    }
                }
                requestSelectFileCode -> {
                    try {
                        Log.e("path", Uri.decode(data!!.data!!.toString()))
                        if (FileUtil.isAndroidQorAbove) {
                            pickImageFresco.setImageURI(data.data, ctx)
                            pickImage.setImageURI(data.data)
                            copyFileToPrivateArea(data.data)
                        } else {
                            getRealPathFromURI(data.data!!)?.apply {
                                pickImage.setImageBitmap(BitmapFactory.decodeFile(this))
                                ImageLoader.loadImageByPath(pickImageFresco, this)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun copyFileToPrivateArea(contentUri: Uri?) {
        contentUri ?: return

        val destFile = FileUtil.copyFileFromPublicToPrivateAtTargetQ(
            ctx,
            contentUri,
            FileUtil.getFile(
                ctx,
                "transfer",
                FileUtil.getPictureName("transfre_"),
                isInData = false
            )
        )

        if (destFile != null && destFile.exists()) {
            ImageLoader.loadImageByPath(dispImageInner, destFile.absolutePath)
        }
    }
}

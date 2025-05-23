package com.soli.newframeapp

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Images.ImageColumns
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.download.FileDownloadProcess
import com.soli.libcommon.util.*
import com.soli.libcommon.view.loading.LoadingType
import com.soli.newframeapp.databinding.ActivityAndroid7Binding
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException


/**
 *  Android 相关文件适配功能测试
 * @author Soli
 * @Time 2018/8/29 09:47
 */
class Android7Activity : BaseActivity<ActivityAndroid7Binding>() {

    private val requestSelectFileCode = 32
    private val requestOpenCamera = 33

    private var imagePath: File? = null
    private var cameraUri: Uri? = null

    // Register ActivityResult handler
    private val requestPermissions =
        registerForActivityResult(RequestMultiplePermissions()) { results ->
            // Handle permission requests results
            // See the permission example in the Android platform samples: https://github.com/android/platform-samples
            results.forEach {
                MLog.d("results", "${it.key} ==== ${it.value}")
            }
        }

    private val rxPermissions by lazy { RxPermissions(ctx) }

    override fun initView() {
        title = "Android >=23 Android Q测试"
    }

    override fun initListener() {
        binding.camerText.setOnClickListener { checkPermission() }

        binding.openDocumentPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"

//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "*/*"
//            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(intent, requestSelectFileCode)
        }

        binding.tartgetQ.setOnClickListener {
            downloadPicAndSaveInPublicStorage()
        }
        binding.mediaTarget.setOnClickListener {
            isSet = false
            RxJavaUtil.runOnThread { scanSystemMedia() }

        }

        binding.premissionTest.setOnClickListener {
            premissionTest()
        }
    }

    override fun initData() {
    }


    /**
     *
     */
    private fun premissionTest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    MLog.d("premssion", "no ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
                    if (ContextCompat.checkSelfPermission(
                            ctx!!,
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        MLog.d(
                            "premssion",
                            "request--->ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION"
                        )
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                data = Uri.parse("package:${ctx!!.packageName}")
                            }
                        startActivityForResult(intent, 32)
                    } else {
                        ToastUtils.showShortToast("已经有权限了")
                    }
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                MLog.d("premssion", "有--->ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
                if (!havePermission()) {
                    requestRermission()
                } else {
                    MLog.d("premssion", "有权限，获取数据")
                    lifecycleScope.launch {
                        val images = getImages(contentResolver)
                        images.forEach { image ->
                            MLog.d("Image", "URI: ${image.uri}, Name: ${image.name}, Size: ${image.size}")
                        }
                        withContext(Dispatchers.Main) {
                            binding.pickImageFresco.setImageURI(images.random().uri)
                            binding.pickImage.setImageURI(images.random().uri)
                            copyFileToPrivateArea(images.random().uri)
                        }
                    }
                }
            }
        }
    }

    data class Media(
        val uri: Uri,
        val name: String,
        val size: Long,
        val mimeType: String,
    )

    // Run the querying logic in a coroutine outside of the main thread to keep the app responsive.
// Keep in mind that this code snippet is querying only images of the shared storage.
    suspend fun getImages(contentResolver: ContentResolver): List<Media> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            Images.Media._ID,
            Images.Media.DISPLAY_NAME,
            Images.Media.SIZE,
            Images.Media.MIME_TYPE,
        )

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Query all the device storage volumes instead of the primary only
            Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            Images.Media.EXTERNAL_CONTENT_URI
        }

        val images = mutableListOf<Media>()

        contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(Images.Media._ID)
            val displayNameColumn = cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(Images.Media.SIZE)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(Images.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri, cursor.getLong(idColumn))
                val name = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val mimeType = cursor.getString(mimeTypeColumn)

                val image = Media(uri, name, size, mimeType)
                images.add(image)
            }
        }

        return@withContext images
    }

    private fun requestRermission() {
        // Permission request logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
            MLog.d(
                "premssion", "request--->Manifest.permission.READ_MEDIA_IMAGES,\n" +
                        "                            Manifest.permission.READ_MEDIA_VIDEO,\n" +
                        "                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED"
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
            MLog.d(
                "premssion", "request--->Manifest.permission.READ_MEDIA_IMAGES,\n" +
                        "                            Manifest.permission.READ_MEDIA_VIDEO,\n"
            )
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            MLog.d("premssion", "request--->Manifest.permission.READ_EXTERNAL_STORAGE")
        }
    }

    private fun havePermission(): Boolean {
        val targetSdk = this.applicationContext.applicationInfo.targetSdkVersion
        ToastUtils.showShortToast("target sdk Version $targetSdk")
        if (targetSdk < 33)
            return true
        return if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (
                    ContextCompat.checkSelfPermission(ctx,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(ctx,
                                Manifest.permission.READ_MEDIA_VIDEO
                            ) == PackageManager.PERMISSION_GRANTED
                    )
        ) {
            MLog.d("havePermission", "Full access on Android 13 (API level 33) or higher")
            true
        } else if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            MLog.d("havePermission", "Partial access on Android 14 (API level 34) or higher")
            true
        } else if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            MLog.d("havePermission", "Full access up to Android 12 (API level 32)")
            true
        } else {
            // Access denied
            MLog.e("havePermission", "Access denied")
            false
        }
    }

    private var isSet = false
    private fun scanSystemMedia(): MutableList<String> {
        //查询方式更改
        val cursor: Cursor? = ctx.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                ImageColumns.DATE_ADDED,
                ImageColumns.SIZE,
                ImageColumns.DATA,
                MediaStore.Images.Media._ID
            ),
            null,
            null,
            ImageColumns.DATE_ADDED
        )
        if (cursor == null || !cursor.moveToNext()) return ArrayList()

        val photos = mutableListOf<String>()
        cursor.moveToLast()
        do {
            val path = cursor.getString(cursor.getColumnIndex(ImageColumns.DATA))
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID))
            if (FileUtil.isAndroidQorAbove) {
                val path = MediaStore.Images.Media
                    .EXTERNAL_CONTENT_URI
                    .buildUpon()
                    .appendPath(id.toString()).build().toString()
                MLog.e("path", path)
                if (isAndroidQFileExists(this, path)) {
                    if (!isSet) {
                        isSet = true
                        RxJavaUtil.runOnUiThread { binding.pickImageFresco.setImageURI(path, ctx) }
                    }
                }
            } else {
                if (File(path).exists()) {
                    photos.add(path)
                }
            }
        } while (cursor.moveToPrevious())
        cursor.close()

        return photos
    }

    private fun isAndroidQFileExists(context: Context, path: String?): Boolean {
        var afd: AssetFileDescriptor? = null
        val cr = context.contentResolver
        try {
            val uri = Uri.parse(path)
            afd = cr.openAssetFileDescriptor(uri, "r")
            afd?.close() ?: return false
        } catch (e: FileNotFoundException) {
            return false
        } finally {
            afd?.close()
        }
        return true
    }

    /**
     *
     */
    private fun downloadPicAndSaveInPublicStorage() {

        val downlist = arrayListOf(
            "https://s2.showstart.com/img/2020/20200309/11b861102cb049b487b5f2b99ad011e2_1200_400_101015.0x0.jpg?imageMogr2/thumbnail/!1200x400r/gravity/Center/crop/!1200x400",
            "https://s2.showstart.com/img/2019/20191008/bda32d33f5f44168b96742b2fb29a8c3_600_800_733494.0x0.jpg",
            "https://s2.showstart.com/img/2019/20190826/9f10aa7b205d48ec853413f6cdff3d48_600_800_122780.0x0.jpg",
            "http://img02-xusong.taihe.com/3717fe12434c0f8e1f76571cddc49588_0_0.mp3",
            "https://s2.showstart.com/img/2019/20190809/a2389dcbeb8d42f492c458c4a59b4d3b_600_800_92235.0x0.jpg?",
            "http://img02-xusong.taihe.com/8df5a431b99e217ca757d3cddc7b700b_0_0.mp3",
            "https://s2.showstart.com/img/2018/20181226/06b037a1383146a89eb7d1537bc86841_1280_900_2350842.0x0.jpg"
        )

        showProgress(type = LoadingType.TypeDialog, cancle = false)
        loadingDialg()?.showNumProgress()

        FileDownloadProcess(
            downlist,
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
        val permission = mutableListOf( Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//            Android 10以上就丢弃了
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val temp = rxPermissions.request(*permission.toTypedArray())
            .subscribe { pass ->
                if (pass)
                    takePicture()
                else {
                    ToastUtils.showShortToast("需要相应的权限")
                }
            }
    }

    /**
     * Android10填坑适配指南
     * https://zhuanlan.zhihu.com/p/93947556
     *
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
                        binding.pickImageFresco.setImageURI(cameraUri, ctx)
                        binding.pickImage.setImageURI(cameraUri)
                        copyFileToPrivateArea(cameraUri)
                        ToastUtils.showShortToast(cameraUri?.toString() ?: "Android 10上图片")
                    } else if (imagePath!!.exists()) {
                        ToastUtils.showShortToast("${imagePath!!.absolutePath} 文件存在")
                        binding.pickImage.setImageBitmap(BitmapFactory.decodeFile(imagePath!!.absolutePath))
                        ImageLoader.loadImageByPath(
                            binding.pickImageFresco,
                            imagePath!!.absolutePath
                        )
                    }
                }

                requestSelectFileCode -> {
                    try {
                        Log.e("path", Uri.decode(data!!.data!!.toString()))
                        if (FileUtil.isAndroidQorAbove) {
                            binding.pickImageFresco.setImageURI(data.data, ctx)
                            binding.pickImage.setImageURI(data.data)
                            copyFileToPrivateArea(data.data)
                        } else {
                            getRealPathFromURI(data.data!!)?.apply {
                                binding.pickImage.setImageBitmap(BitmapFactory.decodeFile(this))
                                ImageLoader.loadImageByPath(binding.pickImageFresco, this)
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
            ImageLoader.loadImageByPath(binding.dispImageInner, destFile.absolutePath)
        }
    }
}

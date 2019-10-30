package com.soli.newframeapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.download.FileDownloadProcess
import com.soli.libcommon.util.FileUtil
import com.soli.libcommon.util.RxJavaUtil
import com.soli.libcommon.util.ToastUtils
import com.soli.libcommon.view.root.LoadingType
import com.soli.permissions.RxPermissions
import kotlinx.android.synthetic.main.activity_android7.*
import java.io.File


/**
 *
 * @author Soli
 * @Time 2018/8/29 09:47
 */
class Android7Activity : BaseActivity() {

    private var imagePath: File? = null
    private val rxPermissions by lazy { RxPermissions(ctx) }

    override fun getContentView() = R.layout.activity_android7

    override fun initView() {
        title = "Android >=23 测试"
    }

    override fun initListener() {
        camerText.setOnClickListener { checkPermission() }

        openDocumentPicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent, 23)
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
            "https://s2.showstart.com/img/2019/20191008/bda32d33f5f44168b96742b2fb29a8c3_600_800_733494.0x0.jpg",
            "https://s2.showstart.com/img/2019/20190826/9f10aa7b205d48ec853413f6cdff3d48_600_800_122780.0x0.jpg",
            "https://s2.showstart.com/img/2019/20190809/a2389dcbeb8d42f492c458c4a59b4d3b_600_800_92235.0x0.jpg?",
            "https://s2.showstart.com/img/2018/20181226/06b037a1383146a89eb7d1537bc86841_1280_900_2350842.0x0.jpg"
        )

        showProgress(LoadingType.TypeDialog)
        FileDownloadProcess(downlist,
            customSavePath = { url, origin ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    File(FileUtil.getUserCanSeeDir(ctx), FileUtil.getFileName("joker_", url))
                else
                    origin
            })
        { isSuccess, fileInfo ->
            dismissProgress()
            if (isSuccess) {
                RxJavaUtil.runOnThread {
                    fileInfo?.forEach { info ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            FileUtil.storeFileInPublicAtTargetQ(
                                ctx,
                                File(info.filePath)
                            ) else
                            FileUtil.scanMediaForFile(ctx, info.filePath)
                    }
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
     *
     */
    private fun takePicture() {
        imagePath =
            FileUtil.getFile(ctx, "capture", "${System.currentTimeMillis()}_picture.jpeg", false)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //拍照结果输出到这个uri对应的file中
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //对这个uri进行授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                FileProvider.getUriForFile(
                    ctx,
                    "${BuildConfig.APPLICATION_ID}.fileProvider",
                    imagePath!!
                )
            } else {
                Uri.fromFile(imagePath)
            }
        )
        // 打开Camera
        startActivityForResult(intent, 21)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 21 && resultCode == Activity.RESULT_OK) {
            if (imagePath!!.exists()) {
                ToastUtils.showShortToast("${imagePath!!.absolutePath} 文件存在")
            }
        }
    }
}

package com.soli.newframeapp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.ApiResult
import com.soli.libCommon.util.FileUtil
import com.soli.libCommon.util.ToastUtils
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

    private val dialog by lazy {
        val dialog = ProgressDialog(ctx)
        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        dialog.setTitle("上传")
        dialog.setMessage("正在上传，请稍后...")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setCancelable(false)
        dialog
    }

    override fun getContentView() = R.layout.activity_android7

    override fun initView() {
        title = "Android >=23 测试"
    }

    override fun initListener() {
        camerText.setOnClickListener { checkPermission() }

    }

    override fun initData() {
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
        imagePath = FileUtil.getFile(ctx, "capture", "${System.currentTimeMillis()}_picture.jpeg", false)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //拍照结果输出到这个uri对应的file中
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //对这个uri进行授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                FileProvider.getUriForFile(ctx, "${BuildConfig.APPLICATION_ID}.fileProvider", imagePath!!)
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
//                ToastUtils.showShortToast("${imagePath!!.absolutePath} 文件存在")

                uploadFile(imagePath!!.absolutePath)
            }
        }
    }

    private fun uploadFile(path: String) {
        dialog.setProgressNumberFormat("%1d KB/%2d KB")
        dialog.show()
        dialog.progress = 0

        ApiHelper.Builder()
            .baseUrl("http://upload-joker.taihe.com/")
            .fileUrl(path)
            .build()
            .uploadFile({ result: ApiResult<String>? ->
                dialog.dismiss()
//                if (result!!.isSuccess)
//                    if (result.result) {
////                        ToastUtils.showLongToast("文件下载成功！：${result.fullData}")
////                        InstallUtil.install(ctx, result.result)
//                    } else
//                        ToastUtils.showShortToast(result.errormsg)
            }, { _, bytesRead, fileSize, _ ->
                dialog.max = (fileSize / 1024).toInt()
                dialog.progress = (bytesRead / 1024).toInt()
            })
    }
}

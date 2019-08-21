package com.soli.newframeapp

import android.Manifest
import android.os.Handler
import android.view.View
import com.dhh.rxlifecycle2.RxLifecycle
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.net.ResultCode
import com.soli.libcommon.util.NetworkUtil
import com.soli.libcommon.util.ToastUtils
import com.soli.libcommon.util.openActivity
import com.soli.libcommon.view.root.LoadingType
import com.soli.newframeapp.autowrap.AutoWrapLayoutTestActivity
import com.soli.newframeapp.bottomsheet.BottomSheetTestActivity
import com.soli.newframeapp.demo.TestTopSpecialActivity
import com.soli.newframeapp.download.DownloadTestActivity
import com.soli.newframeapp.net.NetWorkTestActivity
import com.soli.newframeapp.net.WebviewActivity
import com.soli.newframeapp.palette.PaletteActivity
import com.soli.newframeapp.pic.PicDealActivity
import com.soli.newframeapp.pubu.PubuTestActivity
import com.soli.newframeapp.span.SpecialSpanActivity
import com.soli.newframeapp.toast.CustomToastActivity
import com.soli.permissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {

    private val retryIndex: Int = 1
    private var retry: Int = 0
    private val rxPermissions by lazy { RxPermissions(ctx) }


    override fun needSliderActivity() = false

    override fun getContentView() = R.layout.activity_main

    override fun initView() {
        title = "New Frame"
    }

    override fun setTitle(title: CharSequence) {
        rootView.setTitleLeft(title)
    }

    override fun setTitle(titleId: Int) {
        rootView.setTitleLeft(titleId)
    }

    override fun initListener() {

        fragmentTest.setOnClickListener(this)
        LauchActivity.setOnClickListener(this)
        netWorkTest.setOnClickListener(this)
        fileDownload.setOnClickListener(this)
        webViewTest.setOnClickListener(this)
        _23Test.setOnClickListener(this)
        websocket.setOnClickListener(this)
        btnColorMatrix.setOnClickListener(this)
        btnBottomSheet.setOnClickListener(this)
        btnCustFlex.setOnClickListener(this)
        btnSpecialDemo.setOnClickListener(this)
        btnpubo.setOnClickListener(this)
        myToast.setOnClickListener(this)
        palette.setOnClickListener(this)
        richText.setOnClickListener(this)

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
                errorHappen(1, ApiResult(ResultCode.NETWORK_TROBLE, "测试")) {
                    retry++
                    loadingErrorTest()
                }
        }, 2000)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.LauchActivity -> openActivity<SecondAcitivity>()
            R.id.fragmentTest -> openActivity<FragmentTestActivity>()
            R.id.netWorkTest -> openActivity<NetWorkTestActivity>()
            R.id.fileDownload -> checkPermission()
            R.id.webViewTest -> openActivity<WebviewActivity>()
            R.id._23Test -> openActivity<Android7Activity>()
            R.id.websocket -> openActivity<WebsocketActivity>()
            R.id.btnColorMatrix -> openActivity<PicDealActivity>()
            R.id.btnBottomSheet -> openActivity<BottomSheetTestActivity>()
            R.id.btnCustFlex -> openActivity<AutoWrapLayoutTestActivity>()
            R.id.btnSpecialDemo -> openActivity<TestTopSpecialActivity>()
            R.id.btnpubo -> openActivity<PubuTestActivity>()
            R.id.myToast -> openActivity<CustomToastActivity>()
            R.id.palette -> openActivity<PaletteActivity>()
            R.id.richText -> openActivity<SpecialSpanActivity>()
        }
    }

    /**
     *
     */
    private fun checkPermission() {
        val diapose =
            rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                .compose(RxLifecycle.with(this).bindToLifecycle())
                .subscribe { pass ->
                    if (pass)
                        openActivity<DownloadTestActivity>()
                    else {
                        ToastUtils.showShortToast("需要文件读写权限")
                    }
                }
    }
}

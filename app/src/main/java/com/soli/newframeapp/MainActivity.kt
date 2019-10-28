package com.soli.newframeapp

import android.Manifest
import android.os.Handler
import android.util.Log
import android.view.View
import com.dhh.rxlifecycle2.RxLifecycle
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.net.ResultCode
import com.soli.libcommon.util.*
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
        rsaTest.setOnClickListener(this)

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
            R.id.rsaTest ->rsaTest()
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


    val publick_key = """
        -----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClXmeNn3ymwb2vslaHHFr3NiBz
pGCRSSpWUc/VHDcsoeMOV7A918jFaXAOWxog8molRDLuliZqv7qrvLFOvq6LGwfW
TCb/StokZglK/GmWdxmjmPelXs3MSQStgWYRyu9Jz2lxDo+G2Bc5ch37A9TCg6FS
zgqTDtyxDZquhSMDbQIDAQAB
-----END PUBLIC KEY-----
    """.trimIndent()

    val private_key = """
        -----BEGIN RSA PRIVATE KEY-----
MIICXQIBAAKBgQClXmeNn3ymwb2vslaHHFr3NiBzpGCRSSpWUc/VHDcsoeMOV7A9
18jFaXAOWxog8molRDLuliZqv7qrvLFOvq6LGwfWTCb/StokZglK/GmWdxmjmPel
Xs3MSQStgWYRyu9Jz2lxDo+G2Bc5ch37A9TCg6FSzgqTDtyxDZquhSMDbQIDAQAB
AoGAEipHl8AAMlUv3//oD1lnCKbSc8GHtg3ib6729ILv8KArz+SEAJcWf9DwNTN+
sEXQsR1HtvuZZrp+5+SHWY4KoCH1Bs+vfbx2fS/hkwf3JvCyFTFOv18+nNqaQyik
SDFcxhsMElmJH45QoqstWIky30PfiFB0bq91BjG2iunHMEUCQQDTNIxOevm+/PEm
+VxzIuGoH7OROtoxKebfejIKI5yo/eWUNZ1W7aUaNIG5bp/vxlXmvOuEgI/9NdTz
KiRMB5DbAkEAyHEmeejZxeQKTZRTnZMhD2Akd9OHOPLxkPkwys+jewXtyR46SamY
XWc3dh1/MD3GO/8+tpObu+z/WmMBMnQrVwJAXy9vjG8f31Nf25DGeZ1e1cZzxyAe
9clMo6sOokMqd3712LXREzxHDGhdjpSswANC85pxCmZmflekgXKcqSc/wQJBAK2B
lMbOk0RDo8+H5+Fs7J88oBTBnDnlwsm1i1Dj8CWb+juv2NDO579ii5XI7sI5lxF0
Xzr4B0TjYB9DuFOOT70CQQCKJ+hyKOtkrXI+9obKE3PP7untg1X8HvSkLZdY2Bah
mtPFZWCyL3HNRSURFiOCfVzLk9LG+a+qgqXL9gw0jMK/
-----END RSA PRIVATE KEY-----
    """.trimIndent()


    private fun rsaTest() {
        val endString =
            "{\"self_userid\":\"100016\",\"app_v\":\"1.13.1\",\"sys_v\":\"27\",\"userId\":\"100016\",\"sysModel\":\"MI 5X\",\"_authOnce\":\"94h9ggj9\",\"_authKey\":\"170894c09f7127d2ed3e084b4a80c231\",\"terminal\":\"android\",\"app_o\":\"0\",\"uvkey\":\"J0/bLJNwuaKp7oNrxtHAcBOCwpR5t53TCWl3xX8oY3d5xbsDx23Nh4X0F1fzb0n9RcGP0NpS1gZ0kYBJLJD6sCMakzYviFRRFw16F4pkCG3E0xkemPNLGAhES5EJ5lhDsE9Wh3CMSB+aUTswzAcz5gHb+rDKhuCSPlVcua3JTQ4=\"}"
        val encode = RSAUtils.encryptDataByPublickey(endString, dealPublckKey())
        Log.e("RSA", "加密的：$encode")
        val decode = RSAUtils.decryptDataByPrivatekey(encode, dealPrivateKey())
        Log.e("RSA", "解密的：$decode")
    }


    private fun dealPublckKey(): String =
        publick_key.replace("-----BEGIN PUBLIC KEY-----", "").replace(
            "-----END PUBLIC KEY-----",
            ""
        ).replace("\n", "")

    private fun dealPrivateKey(): String = private_key.replace(
        "-----BEGIN RSA PRIVATE KEY-----",
        ""
    ).replace("-----END RSA PRIVATE KEY-----", "").replace("\n", "")
}

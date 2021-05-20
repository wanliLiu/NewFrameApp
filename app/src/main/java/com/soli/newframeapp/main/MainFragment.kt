package com.soli.newframeapp.main

import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.forEach
import com.dhh.rxlifecycle2.RxLifecycle
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.net.ResultCode
import com.soli.libcommon.util.*
import com.soli.libcommon.view.loading.LoadingType
import com.soli.newframeapp.*
import com.soli.newframeapp.autowrap.AutoWrapLayoutTestActivity
import com.soli.newframeapp.bottomsheet.BottomSheetTestActivity
import com.soli.newframeapp.demo.TestTopSpecialActivity
import com.soli.newframeapp.download.DownloadTestActivity
import com.soli.newframeapp.drag.DragFragment
import com.soli.newframeapp.event.openFragment
//import com.soli.newframeapp.flutter.FlutterEntranceActivity
import com.soli.newframeapp.fragment.LaunchUIHome
import com.soli.newframeapp.motion.MotionLayoutFragment
import com.soli.newframeapp.net.NetWorkTestActivity
import com.soli.newframeapp.net.WebviewActivity
import com.soli.newframeapp.palette.PaletteActivity
import com.soli.newframeapp.pic.PicDealFragment
import com.soli.newframeapp.pubu.PubuTestActivity
import com.soli.newframeapp.scanfile.ScanFileFagment
import com.soli.newframeapp.span.SpecialSpanFragment
import com.soli.newframeapp.toast.CustomToastActivity
import com.soli.permissions.RxPermissions
import kotlinx.android.synthetic.main.fragment_main.*

/**
 *
 * @author Soli
 * @Time 2020/6/1 13:55
 */
class MainFragment : BaseToolbarFragment() {

    private val retryIndex: Int = 1
    private var retry: Int = 0
    private val rxPermissions by lazy { RxPermissions(this) }


    override fun needSwipeBack() = false

    override fun getContentView() = R.layout.fragment_main

    override fun initView() {
        setTitle("New Frame")
        homeLayout.forEach {
            val click: (View) -> Unit = { child ->
                onHomeClick(child)
            }
            it.clickView(click)
        }
    }

    override fun setTitle(title: CharSequence) {
        rootView.setTitleLeft(title)
    }

    override fun setTitle(titleId: Int) {
        rootView.setTitleLeft(titleId)
    }

    override fun initListener() {

        progressInTest.setOnClickListener {
            showProgress()
            Handler(Looper.getMainLooper()).postDelayed({
                dismissProgress()
            }, 2000)
        }

        progressDialogTest.setOnClickListener {
            showProgress(type = LoadingType.TypeDialog)
            Handler(Looper.getMainLooper()).postDelayed({
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
        Handler(Looper.getMainLooper()).postDelayed({
            dismissProgress()
            if (retry < retryIndex)
                errorHappen<Any>(1, ApiResult(ResultCode.NETWORK_TROBLE, "测试")) {
                    retry++
                    loadingErrorTest()
                }
        }, 2000)
    }

    /**
     *
     */
    private fun onHomeClick(v: View) {
        when (v.id) {
            R.id.LauchActivity -> openActivity<SecondAcitivity>()
            R.id.fragmentTest -> openActivity<FragmentTestActivity>()
            R.id.netWorkTest -> openActivity<NetWorkTestActivity>()
            R.id.fileDownload -> checkStorePermission { openActivity<DownloadTestActivity>() }
            R.id.webViewTest -> openActivity<WebviewActivity>()
            R.id._23Test -> openActivity<Android7Activity>()
            R.id.websocket -> openActivity<WebsocketActivity>()
            R.id.btnColorMatrix -> openFragment(PicDealFragment(), newActivity = true)
            R.id.btnBottomSheet -> openActivity<BottomSheetTestActivity>()
            R.id.btnCustFlex -> openActivity<AutoWrapLayoutTestActivity>()
            R.id.btnSpecialDemo -> openActivity<TestTopSpecialActivity>()
            R.id.btnpubo -> openActivity<PubuTestActivity>()
            R.id.myToast -> openActivity<CustomToastActivity>()
            R.id.palette -> openActivity<PaletteActivity>()
            R.id.richText -> start(SpecialSpanFragment())
            R.id.rsaTest -> rsaTest()
            R.id.fragmentFramework -> openActivity<LaunchUIHome>()
            R.id.motionLayout -> start(MotionLayoutFragment())
            R.id.scanFile -> checkStorePermission {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    Toast.makeText(ctx, "Android Q以上就没法用了哦", Toast.LENGTH_SHORT).show()
                else
                    start(ScanFileFagment())
            }
            R.id.dragTest -> start(DragFragment())
//            R.id.flutterIn -> openActivity<FlutterEntranceActivity>(
//                "initial_route" to "/",
//                "background_mode" to "opaque",
//                "destroy_engine_with_activity" to true
//            )
            else -> Toast.makeText(ctx, "没有需要点击打开的", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     *
     */
    private fun checkStorePermission(callBack: () -> Unit) {
        val diapose =
            rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                .compose(RxLifecycle.with(this).bindToLifecycle())
                .subscribe { pass ->
                    if (pass)
                        callBack()
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
        val pubEn = RSAUtils.encryptDataByPublickey(endString, dealPublckKey())
        Log.e("RSA", "公钥加密的：$pubEn")
        val priDec = RSAUtils.decryptDataByPrivatekey(pubEn!!, dealPrivateKey())
        Log.e("RSA", "私钥解密的：$priDec")

        val priEn = RSAUtils.encryptDataByPrivatekey(endString, dealPrivateKey())
        Log.e("RSA", "私钥加密的：$priEn")
        val pubDec = RSAUtils.decryptDataByPublickey(priEn!!, dealPublckKey())
        Log.e("RSA", "公钥解密的：$pubDec")

        val key =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDN/gr5JMU83YboVR1zdoPhhg8FrWE9OokjTILsb9qzJ82gpmXEqpcCoWm9zR0BScx2ZE2SJdQtKnhIA7TkQWvTELyYy4bF1+Vhcg9t/gQhKcxWw4Yu/Hi5MxnxAJ8VxpbJbGRImEsitWAf2Jw4a2NLpJvIl371IcQfzx658/uS+wIDAQAB"
        val value =
            "KYeO27TKBgT46yIARG2UuaElX7QfmSAKKkfPt3KjtTYV4JQ1VuMpd05ud75r5CzgpeM3quwKugGs8N7YFjdsnQtlduVdfA/24yecX2bN/RbDdLR5yOLnFm7jm5JflfUoYiL0FL3SETI3GSbQMSlvkqGAkj2G4+08w5SdjkisyMscd2tiam4E7q2hFVg9pHxIyfJaeViCfk18SxDrI1Lbjk6nlvEUv/zvwaoUTheiRoLHfyShTUYW4Qlg0lG1NOYulr/aA6Y4dGnlUBLYzmEoCsm/kgCesYnntZf1BUpvtEz+1m7yvFk5vL+Zj0x6+rXVMrroZ3cE2ky5mMzyIwlzVQ=="
        val dsd = RSAUtils.decryptDataByPublickey(value, key)
        Log.e("RSA", "dsdsd：$dsd")
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
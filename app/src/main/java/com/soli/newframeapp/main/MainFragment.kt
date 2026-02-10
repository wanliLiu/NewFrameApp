package com.soli.newframeapp.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.forEach
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.kiwisec.floatwindow.FloatWindow
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.libcommon.base.Constant
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.net.ResultCode
import com.soli.libcommon.util.MLog
import com.soli.libcommon.util.NetworkUtil
import com.soli.libcommon.util.RSAUtils
import com.soli.libcommon.util.RxJavaUtil
import com.soli.libcommon.util.ScreenHeight
import com.soli.libcommon.util.ScreenWidth
import com.soli.libcommon.util.ToastUtils
import com.soli.libcommon.util.clickView
import com.soli.libcommon.util.md5String
import com.soli.libcommon.util.openActivity
import com.soli.libcommon.view.loading.LoadingType
import com.soli.newframeapp.Android7Activity
import com.soli.newframeapp.BuildConfig
import com.soli.newframeapp.FragmentTestActivity
import com.soli.newframeapp.R
import com.soli.newframeapp.SecondAcitivity
import com.soli.newframeapp.WebsocketActivity
import com.soli.newframeapp.access.AccessibilityUtil
import com.soli.newframeapp.access.AutoClickByHierachryObservable
import com.soli.newframeapp.access.AutoClickObservable
import com.soli.newframeapp.access.KiwiAccessibilityService
import com.soli.newframeapp.access.PauseControl
import com.soli.newframeapp.access.ViewStateListenerAdapter
import com.soli.newframeapp.access.registerEvent
import com.soli.newframeapp.audio.AudioRecordActivity
import com.soli.newframeapp.autowrap.AutoWrapLayoutTestActivity
import com.soli.newframeapp.bottomsheet.BottomSheetTestActivity
import com.soli.newframeapp.databinding.FragmentMainBinding
import com.soli.newframeapp.demo.TestTopSpecialActivity
import com.soli.newframeapp.download.DownloadTestActivity
import com.soli.newframeapp.drag.DragFragment
import com.soli.newframeapp.event.startFragment
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
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.concurrent.thread

/**
 *
 * @author Soli
 * @Time 2020/6/1 13:55
 */
class MainFragment : BaseToolbarFragment<FragmentMainBinding>() {

    private val retryIndex: Int = 1
    private var retry: Int = 0
    private val rxPermissions by lazy { RxPermissions(this) }
    private val pauseControl = PauseControl()

    override fun needSwipeBack() = false

    override fun initView() {
        setTitle("New Frame")
        binding.homeLayout.forEach {
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

        binding.progressInTest.setOnClickListener {
            showProgress()
            Handler(Looper.getMainLooper()).postDelayed({
                dismissProgress()
            }, 2000)
        }

        binding.progressDialogTest.setOnClickListener {
            showProgress(type = LoadingType.TypeDialog)
            Handler(Looper.getMainLooper()).postDelayed({
                dismissProgress()
            }, 2000)
        }

        binding.loaddingErroTest.setOnClickListener {
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
            if (retry < retryIndex) errorHappen<Any>(
                1,
                ApiResult(ResultCode.NETWORK_TROBLE, "测试")
            ) {
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
//            R.id.fileDownload -> checkStorePermission { openActivity<DownloadTestActivity>() }
            R.id.fileDownload -> openActivity<DownloadTestActivity>()
            R.id.webViewTest -> openActivity<WebviewActivity>()
            R.id.Test23 -> openActivity<Android7Activity>()
            R.id.websocket -> openActivity<WebsocketActivity>()
            R.id.btnColorMatrix -> context?.startFragment<PicDealFragment>()//openFragment(PicDealFragment(), newActivity = true)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Toast.makeText(
                    ctx,
                    "Android Q以上就没法用了哦",
                    Toast.LENGTH_SHORT
                ).show()
                else start(ScanFileFagment())
            }

            R.id.autoClick -> autoClickTest()
            R.id.dragTest -> start(DragFragment())
//            R.id.flutterIn -> openActivity<FlutterEntranceActivity>(
//                "initial_route" to "/",
//                "background_mode" to "opaque",
//                "destroy_engine_with_activity" to true
//            )
            R.id.audioRecored -> openActivity<AudioRecordActivity>()
            else -> Toast.makeText(ctx, "没有需要点击打开的", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     *
     */
    private fun autoClickTest() {

        if (BuildConfig.IsRom or AccessibilityUtil.isOpen(
                requireContext(),
                requireActivity().packageName,
                KiwiAccessibilityService::class.java
            ) && rxPermissions.isGranted(Manifest.permission.SYSTEM_ALERT_WINDOW)
        ) {
            thread {
                if (KiwiAccessibilityService.instance == null) {
                    //开启无障碍
                    KiwiAccessibilityService.startService(requireContext())

                    //检测无障碍启动
                    for (i in 0..10) {
                        if (KiwiAccessibilityService.instance != null) break
                        Thread.sleep(1000)
                    }


                } else {
                    MLog.d("AutoClickObservable", "无障碍服务开启成功")
                }

                RxJavaUtil.runOnUiThread {
                    if (KiwiAccessibilityService.instance == null) {
                        ToastUtils.showShortToast("无障碍服务开启失败，请重启手机试一下")
                    } else {
                        showControlView()
                        showTest()
                    }
                }
            }
        } else {
            if (!AccessibilityUtil.isOpen(
                    requireContext(),
                    requireActivity().packageName,
                    KiwiAccessibilityService::class.java
                )
            ) {
                requireActivity().startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                )
            } else if (!rxPermissions.isGranted(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                val diapose = rxPermissions.request(
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                ).subscribe { pass ->
                }
            }
        }
    }

    /**
     *
     */
    private var autoCLickSubscribe: Disposable? = null
    private var controlImageView: ImageView? = null
    private fun showControlView() {
        controlImageView = ImageView(context).apply { id = R.id.id_control }
        var paused = true
        pauseControl.change(paused)
        controlImageView!!.setImageResource(if (paused) R.drawable.start else R.drawable.pause)
        FloatWindow.with(requireActivity().application)
            .setTag("control")
            .setView(controlImageView!!)
            .setWidth(ViewStateListenerAdapter.viewSize)
            .setHeight(ViewStateListenerAdapter.viewSize)
            .setX(0)
            .setY((requireContext().ScreenHeight * 0.4).toInt())
            .setDesktopShow(true)
            .setViewStateListener(ViewStateListenerAdapter("control"))
            .build()

        controlImageView!!.setOnClickListener {
            autoCLickSubscribe?.dispose()
            paused = !paused
            pauseControl.change(paused)

            if (paused) {
                controlImageView!!.setImageResource(R.drawable.start)
//                FloatWindow.get().show()
            } else {
                controlImageView!!.setImageResource(R.drawable.pause)
//                FloatWindow.get().hide()
                startClick()
            }
        }

        FloatWindow.get("control")?.show()
        rootView.getContentView()?.findViewById<View>(R.id.autoClick)?.visibility = View.GONE
    }

    private fun showTest() {
        val imageView = ImageView(context).apply { id = R.id.id_demo }
        imageView.setImageResource(R.drawable.screenshot)
        FloatWindow.with(requireActivity().application)
            .setView(imageView)
            .setTag("click")
            .setWidth(ViewStateListenerAdapter.viewSize)
            .setHeight(ViewStateListenerAdapter.viewSize)
            .setX(Constant.context.ScreenWidth - ViewStateListenerAdapter.viewSize)
            .setY((Constant.context.ScreenHeight  * 0.3).toInt())
            .setDesktopShow(true)
            .setViewStateListener(ViewStateListenerAdapter("click"))
            .build()

        imageView.clickView {
            thread {
                //KiwiAccessibilityService.instance?.printAllNode()
                KiwiAccessibilityService.instance?.let { service ->
                    service.windows.let { windows ->
                        MLog.d(AutoClickObservable.TAG, "windows size: ${windows.size}")
                        windows.forEach { win ->
                            if (win.isActive) {
                                MLog.d(
                                    AutoClickObservable.TAG,
                                    "-------->hash:${win.hashCode()} \n $win \n -------->root node hash:${win.root.hashCode()} \n root node：\n ${win.root} \n parent windows: \n${win.parent} \n getChildCount= ${win.childCount} "
                                )
                            }
                        }
                    }
                    val canClicklist = JSONArray()
                    val hierachy =
                        dumpHierachry(service.rootInActiveWindow, canClicklist).toJSONString()
                    MLog.d(
                        AutoClickObservable.TAG,
                        "canClicklist = ${canClicklist.size} current hierachy MD5 = ${hierachy.md5String()} \n "
                    )
                }
            }
        }
        FloatWindow.get("click")?.show()
    }

    /**
     *
     */
    private fun decideCanClick(node: AccessibilityNodeInfo, list: JSONArray) {
        if (node.isEnabled && node.isVisibleToUser) {
            if (node.isClickable) { //|| node.isFocusable || node.isCheckable
                list.add(node)
            }
        }
    }


    /**
     *
     */
    private fun addNode(node: AccessibilityNodeInfo, json: JSONObject) {
        json["packageName"] = node.packageName ?: ""
        json["className"] = node.className ?: ""
//        json["text"] = node.text ?: ""
//        json["contentDescription"] = node.contentDescription ?: ""
//        json["childCount"] = node.childCount
//        json["checkable"] = node.isCheckable
//        json["checked"] = node.isChecked
//        json["focusable"] = node.isFocusable
//        json["focused"] = node.isFocused
//        json["selected"] = node.isSelected
//        json["clickable"] = node.isClickable
//        json["enabled"] = node.isEnabled
//        json["scrollable"] = node.isScrollable
//        json["visible"] = node.isVisibleToUser
        json["viewIdResName"] = node.viewIdResourceName ?: ""
//        var zoom = Rect()
//        node.getBoundsInScreen(zoom)
//        json["boundsInParent"] = zoom.toString()
//        node.getBoundsInParent(zoom)
//        json["boundsInScreen"] = zoom.toString()
    }

    /**
     *
     */
    private fun dumpHierachry(node: AccessibilityNodeInfo, canClickNode: JSONArray): JSONObject {
        val hierarchy = JSONObject()
        if (node.childCount > 0) {
            addNode(node, hierarchy)
            decideCanClick(node, canClickNode)
            val array = JSONArray()
            for (index in 0 until node.childCount) {
                node.getChild(index)?.apply { array.add(dumpHierachry(this, canClickNode)) }
            }
            hierarchy["childs"] = array
        } else {
            addNode(node, hierarchy)
            decideCanClick(node, canClickNode)
        }

        return hierarchy
    }


    /**
     *
     */
    private fun startClick() {
        KiwiAccessibilityService.instance!!.registerEvent { event ->
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                val text =
                    event.text.firstOrNull().let { it ?: event.contentDescription }?.toString()
                        ?.replace(Regex("\\s"), "")
                MLog.d(AutoClickByHierachryObservable.TAG, "click：$text")
            }
        }

        autoCLickSubscribe = AutoClickByHierachryObservable(
            KiwiAccessibilityService.instance!!, "com.cnzz.gnq1",
//            "com.bankscene.bes.financialmall",
//            "com.taihe.fans",
//            "com.zeekrlife.mobile",
//            "com.ting.mp3.android",
//            "com.showstartfans.activity",
            { pauseControl.isPause() }, pauseControl, true
        ).observable().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                controlImageView?.performClick()
            }, {})
    }

    /**
     *
     */
    private fun checkStorePermission(callBack: () -> Unit) {
        val diapose = rxPermissions.request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe { pass ->
            if (pass) callBack()
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
            "-----END PUBLIC KEY-----", ""
        ).replace("\n", "")

    private fun dealPrivateKey(): String = private_key.replace(
        "-----BEGIN RSA PRIVATE KEY-----", ""
    ).replace("-----END RSA PRIVATE KEY-----", "").replace("\n", "")
}
package com.soli.newframeapp.toast

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.soli.libCommon.base.BaseActivity
import kotlinx.android.synthetic.main.activity_toast.*


/**
 *
 * @author Soli
 * @Time 2019/1/9 15:31w
 */
class CustomToastActivity : BaseActivity() {

    var index = 0

    private val mwindowManager by lazy { ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
//    private val rxPermission by lazy { RxPermissions(ctx) }

    override fun getContentView() = com.soli.newframeapp.R.layout.activity_toast

    override fun initView() {
        title = "自定义toast"
    }

    override fun initListener() {

        toast.setOnClickListener {
            //            ToastUtils.showShortToast("我长度但是看到了斯柯达了SDK类似都开始来得快熟练度说的了${++index}")
//            ToastUtils.showLongToast("我长度但是看到了斯柯达了SDK类似都开始来得快熟练度说的了${++index}")
//            Toast.makeText(ctx,"我长度但是看到了斯柯达了SDK类似都开始来得快熟练度说的了${++index}",Toast.LENGTH_SHORT).show()

//            rxPermission.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
//                .subscribe {
//                    if (it)
//
//                }

            test()

        }
    }

    override fun initData() {
    }

    private fun test() {

        val textView = TextView(ctx)
        textView.gravity = Gravity.CENTER
        textView.setBackgroundColor(Color.BLACK)
        textView.text = "zhang phil @ csdn"
        textView.textSize = 10f
        textView.setTextColor(Color.RED)

        //类型是TYPE_TOAST，像一个普通的Android Toast一样。这样就不需要申请悬浮窗权限了。
        val params = WindowManager.LayoutParams()
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION
        params.format = PixelFormat.TRANSLUCENT

        //初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
        params.flags =
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = 300
        params.gravity = Gravity.TOP or Gravity.FILL_HORIZONTAL
        //params.gravity=Gravity.BOTTOM;

        textView.setOnClickListener {
            Toast.makeText(application, "不需要权限的悬浮窗实现", Toast.LENGTH_LONG).show()
            mwindowManager.removeView(it)
        }


        mwindowManager.addView(textView, params)
    }
}
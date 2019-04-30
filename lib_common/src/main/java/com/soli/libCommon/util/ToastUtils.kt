package com.soli.libCommon.util

import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.soli.libCommon.R
import com.soli.libCommon.base.Constant


/**
 * Toast相关工具类
 */
object ToastUtils {

    private val sHandler = Handler(Looper.getMainLooper())

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    fun showShortToastSafe(text: String) {
        sHandler.post { showToast(text, Toast.LENGTH_SHORT) }
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源Id
     */
    fun showShortToastSafe(@StringRes resId: Int) {
        sHandler.post { showToast(resId, Toast.LENGTH_SHORT) }
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    fun showShortToastSafe(@StringRes resId: Int, vararg args: Any) {
        sHandler.post { showToast(resId, Toast.LENGTH_SHORT, *args) }
    }

    /**
     * 安全地显示短时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    fun showShortToastSafe(format: String, vararg args: Any) {
        sHandler.post { showToast(format, Toast.LENGTH_SHORT, *args) }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param text 文本
     */
    fun showLongToastSafe(text: String) {
        sHandler.post { showToast(text, Toast.LENGTH_LONG) }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源Id
     */
    fun showLongToastSafe(@StringRes resId: Int) {
        sHandler.post { showToast(resId, Toast.LENGTH_LONG) }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    fun showLongToastSafe(@StringRes resId: Int, vararg args: Any) {
        sHandler.post { showToast(resId, Toast.LENGTH_LONG, *args) }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    fun showLongToastSafe(format: String, vararg args: Any) {
        sHandler.post { showToast(format, Toast.LENGTH_LONG, *args) }
    }

    /**
     * 显示短时吐司
     *
     * @param text 文本
     */
    fun showShortToast(text: String) {
        showToast(text, Toast.LENGTH_SHORT)
    }

    /**
     * 显示短时吐司
     *
     * @param resId 资源Id
     */
    fun showShortToast(@StringRes resId: Int) {
        showToast(resId, Toast.LENGTH_SHORT)
    }

    /**
     * 显示短时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    fun showShortToast(@StringRes resId: Int, vararg args: Any) {
        showToast(resId, Toast.LENGTH_SHORT, *args)
    }

    /**
     * 显示短时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    fun showShortToast(format: String, vararg args: Any) {
        showToast(format, Toast.LENGTH_SHORT, *args)
    }

    /**
     * 显示长时吐司
     *
     * @param text 文本
     */
    fun showLongToast(text: String) {
        showToast(text, Toast.LENGTH_LONG)
    }

    /**
     * 显示长时吐司
     *
     * @param resId 资源Id
     */
    fun showLongToast(@StringRes resId: Int) {
        showToast(resId, Toast.LENGTH_LONG)
    }

    /**
     * 显示长时吐司
     *
     * @param resId 资源Id
     * @param args  参数
     */
    fun showLongToast(@StringRes resId: Int, vararg args: Any) {
        showToast(resId, Toast.LENGTH_LONG, *args)
    }

    /**
     * 显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    fun showLongToast(format: String, vararg args: Any) {
        showToast(format, Toast.LENGTH_LONG, *args)
    }

    /**
     * 显示吐司
     *
     * @param resId    资源Id
     * @param duration 显示时长
     */
    private fun showToast(@StringRes resId: Int, duration: Int) {
        showToast(Constant.getContext().resources.getText(resId).toString(), duration)
    }

    /**
     * 显示吐司
     *
     * @param resId    资源Id
     * @param duration 显示时长
     * @param args     参数
     */
    private fun showToast(@StringRes resId: Int, duration: Int, vararg args: Any) {
        showToast(String.format(Constant.getContext().resources.getString(resId), *args), duration)
    }

    /**
     * 显示吐司
     *
     * @param format   格式
     * @param duration 显示时长
     * @param args     参数
     */
    private fun showToast(format: String, duration: Int, vararg args: Any) {
        if (!TextUtils.isEmpty(format))
            showToast(String.format(format, *args), duration)
    }

    //全屏和动画的设置方法
    private fun initToast(toast: Toast): WindowManager.LayoutParams? {
        try {
            val mTN = toast.javaClass.getDeclaredField("mTN")
            mTN.isAccessible = true
            val mTNObj = mTN.get(toast)

            val mParams = mTNObj.javaClass.getDeclaredField("mParams")
            mParams.isAccessible = true
            val params = mParams.get(mTNObj) as WindowManager.LayoutParams
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
//            params.windowAnimations = R.style.toastStyle//设置动画, 需要是style类型
            return params
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 显示吐司
     *
     * @param text     文本
     * @param duration 显示时长
     */
    private fun showToast(mtext: String, duration: Int) {
        if (!TextUtils.isEmpty(mtext)) {
//            Toast.makeText(Constant.getContext(),mtext,duration).show()
//            if (toast == null) {


            val toast = Toast.makeText(Constant.getContext(), "", duration)
            val parrams = initToast(toast)
            val layout =
                LayoutInflater.from(Constant.getContext()).inflate(R.layout.view_push_tips_layout, null)//自定义的布局
            toast.view = layout
            toast.setGravity(Gravity.TOP, 0, 0)//从顶部开始显示
//            }
            toast.view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//设置Toast可以布局到系统状态栏的下面
            val toastLayout = toast.view.findViewById<View>(R.id.toastLayout)
            val tips_content = toast.view.findViewById<TextView>(R.id.tips_content)

            tips_content.text = mtext
            parrams?.windowAnimations = R.style.toastStyle_null
            toast.show()
            initToastAnimal(toast, duration)
        }
    }

    /**
     * Toast的动画
     *
     * */
//    private var handler: Handler? = null
//    private var runable: Runnable? = null

    private fun initToastAnimal(toast: Toast, duration: Int) {
        val layout_toast = toast.view.findViewById<View>(R.id.toastLayout)
        layout_toast.startAnimation(AnimationUtils.loadAnimation(Constant.getContext(), R.anim.toast_show))
//        val delay = when (duration) {
//            Toast.LENGTH_LONG -> 3000
//            Toast.LENGTH_SHORT -> 1500
//            else -> 3000
//        }
//
//        if (runable == null) {
//            runable = Runnable {
//                layout_toast.startAnimation(AnimationUtils.loadAnimation(Constant.getContext(), R.anim.toast_hide))
//            }
//        }
//
//        if (handler != null) {
//            handler?.removeCallbacks(runable)
//        }
//        handler = Handler()
//        handler?.postDelayed(
//            runable, delay.toLong()
//        )
    }
}
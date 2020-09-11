package com.soli.libcommon.util

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.doOnLayout
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.AnimatorListenerAdapter
import com.nineoldandroids.animation.AnimatorSet
import com.nineoldandroids.animation.ObjectAnimator
import com.soli.libcommon.R
import com.soli.libcommon.base.Constant
import java.util.*


/**
 * Toast相关工具类
 */
object ToastUtils {

    private data class ToastData(val text: String, val duration: Int)

    private var isToastShow = false
    private val toastList = LinkedList<ToastData>()
    private val sHandler = Handler(Looper.getMainLooper())

    private fun QueueLength() = toastList.size
    /**
     *
     */
    private fun enQueue(data: ToastData) {
        toastList.addLast(data)
    }

    /**
     *
     */
    private fun deQueue(): ToastData? {
        return if (!toastList.isEmpty()) toastList.removeFirst() else null
    }

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

    /**
     *
     */
    private fun showToast(text: String, duration: Int) {
        if (QueueLength() == 0 && !isToastShow) {
            showCustomToast(text, duration)
        } else
            enQueue(ToastData(text, duration))
    }

    /**
     *
     */
    private fun startNext() {
        val data = deQueue()
        data?.apply {
            showCustomToast(text, duration)
        }
        if (data == null)
            isToastShow = false
    }

    /**
     *自定义的Toast类型
     */
    private fun showCustomToast(text: String, duration: Int) {
        if (!TextUtils.isEmpty(text)) {
            isToastShow = true
            val mToast = Toast(Constant.getContext())
            val layout =
                LayoutInflater.from(Constant.getContext()).inflate(R.layout.view_push_tips_layout, null)//自定义的布局
            mToast.view = layout
            mToast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)//从顶部开始显示

            mToast.view?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//设置Toast可以布局到系统状态栏的下面

            layout.doOnLayout {
                val showAnimation = ObjectAnimator.ofFloat(it, "translationY", -it.height * 1.0f, 0f)
                showAnimation.duration = 300
                val pauseAnimation = ObjectAnimator.ofFloat(it, "alpha", 1f, 1f)
                pauseAnimation.duration = if (duration == Toast.LENGTH_SHORT) 1000 else 2000
                val hiedAnimation = ObjectAnimator.ofFloat(it, "translationY", 0f, -it.height * 1.0f)
                hiedAnimation.duration = 300

                val animationSet = AnimatorSet()
                animationSet.setInterpolator(LinearInterpolator())
                animationSet.playSequentially(arrayOf(showAnimation, pauseAnimation, hiedAnimation).toList())
                animationSet.start()
                animationSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        startNext()
                    }
                })
            }

            mToast.view?.findViewById<TextView>(R.id.tips_content)?.text = text
            mToast.duration = Toast.LENGTH_LONG

            mToast.show()
        }
    }
}
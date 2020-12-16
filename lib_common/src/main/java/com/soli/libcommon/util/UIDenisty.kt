package com.soli.libcommon.util

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.soli.libcommon.base.Constant

/**
 *  低成本的尺寸适配方案
 *  参考：[https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA]
 *
 *  px = density * dp;
 *  density = dpi / 160;
 *  px = dp * (dpi / 160);
 *
 * @author Soli
 * @Time 2020/7/1 14:42
 */
object UIDenisty {

    //UI 设计图的尺寸，比如750x1624
    private val defaultUiSize = 360
    private var sNonCompatDensity = 0.0f
    private var sNonCompatScaledDensity = 0.0f

    //是否使用这种方式
    private var needUseThis = true

    /**
     *
     */
    fun setCustomDensity(
        activity: Activity? = null,
        uiDesign: Int = defaultUiSize,
        application: Application = Constant.context as Application
    ) {

        if (!needUseThis) return

        val appDisplayMetrics = application.resources.displayMetrics

        if (sNonCompatDensity == 0.0f) {
            sNonCompatDensity = appDisplayMetrics.density
            sNonCompatScaledDensity = appDisplayMetrics.scaledDensity
            application.registerComponentCallbacks(object : ComponentCallbacks2 by noOpDelegate() {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig.fontScale > 0)
                        sNonCompatScaledDensity = application.resources.displayMetrics.scaledDensity
                }
            })
        }

        val targetDensity = appDisplayMetrics.widthPixels / uiDesign * 1.0f
        val targetScaleDensity = targetDensity * (sNonCompatScaledDensity / sNonCompatDensity)
        val targetDensityDpi = (160 * targetDensity).toInt()

        //application
        appDisplayMetrics.apply {
            density = targetDensity
            scaledDensity = targetScaleDensity
            densityDpi = targetDensityDpi
        }

        //actiivity
        activity?.resources?.displayMetrics?.apply {
            density = targetDensity
            scaledDensity = targetScaleDensity
            densityDpi = targetDensityDpi
        }
    }
}

/**
 * @param uiDesign  设计图的尺寸，比如750x1624  就是375dp
 */
inline fun Activity.setCustomDensity() {
    UIDenisty.setCustomDensity(activity = this)
}

inline fun Application.setCustomDensity() {
    UIDenisty.setCustomDensity(application = this)
}
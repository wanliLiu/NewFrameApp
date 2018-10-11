package com.soli.libCommon.base

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.r0adkll.slidr.Slidr
import com.soli.libCommon.R
import com.soli.libCommon.util.StatusBarUtil

/**
 * @author Soli
 * @Time 18-5-15 下午3:07
 */
abstract class BaseFunctionActivity : AppCompatActivity(), BaseInterface {

    /**
     * 默认竖屏，不支持横竖自定转换
     */
    protected val isScreenOnlyPORTRAIT = true

    /**
     * 上下午context
     */
    protected val ctx by lazy { this }

    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        requestedOrientation = if (isScreenOnlyPORTRAIT) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     *
     */
    private fun setStatusBarColor() {
        val alpha = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 0 else 13
        if (isNeedSliderActivity()) {
            Slidr.attach(this)
        }
        StatusBarUtil.setColorForSwipeBack(this, ctx.resources.getColor(R.color.toolbar_background), alpha)
        StatusBarUtil.setLightMode(ctx)
    }

    open fun isNeedSliderActivity() = true

    /**
     *
     */
    fun showProgressDialog() {
        if (dialog == null) {
            dialog = ProgressDialog(ctx)
            dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }

        if (!dialog!!.isShowing)
            dialog!!.show()
    }

    /**
     *
     */
    fun dissProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    override fun onPause() {
        super.onPause()
        dissProgressDialog()
    }


    /**
     * 加载系统默认设置，字体不随用户设置变化
     * FIXME 注意下这个加上了，ProgressDialog就显示不出来了
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f)//非默认值
            resources
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) {//非默认值
            val newConfig = res.configuration
            newConfig.setToDefaults()//设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }
}
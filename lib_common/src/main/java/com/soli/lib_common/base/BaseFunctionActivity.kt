package com.soli.lib_common.base

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * @author Soli
 * @Time 18-5-15 下午3:07
 */
abstract class BaseFunctionActivity : AppCompatActivity() , BaseInterface {

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

        requestedOrientation = if (isScreenOnlyPORTRAIT) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

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
}
package com.soli.libcommon.base

import android.content.Context

/**
 * @author Soli
 * @Time 18-5-15 上午11:18
 */
object Constant {

    @JvmStatic
    var Debug = false

    /**
     * 服务器地址 todo 根据到时候服务器来填
     */
    var webServer = "http://fb.ci.dev.showstart.com:9527/suapp/"

    private var ctx: Context? = null

    @JvmStatic
    val context: Context
        get() = ctx!!

    /**
     * @param context
     */
    fun init(mCtx: Context) {
        ctx = mCtx.applicationContext
    }
}
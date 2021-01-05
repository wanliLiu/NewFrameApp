package com.soli.libcommon.base

import android.content.Context

/**
 * @author Soli
 * @Time 18-5-15 上午11:18
 */
object Constant {

    private var ctx: Context? = null
    private var isDebug: Boolean = true

    /**
     * 服务器地址 todo 根据到时候服务器来填
     */
    var webServer = "http://fb.ci.dev.showstart.com:9527/suapp/"

    @JvmStatic
    val context: Context
        get() = ctx!!

    /**
     * @param context
     */
    fun init(mCtx: Context) {
        ctx = mCtx.applicationContext
    }

    /**
     * 用于app 调试环境的设置
     * @param debug 觉得app 内部的调试环境
     * @param currentChannel 多环境参数，
     */
    @JvmStatic
    fun envInit(debug: Boolean, currentChannel: String = "") {
        isDebug = debug
    }

    @JvmStatic
    val Debug: Boolean
        get() = isDebug

    @JvmStatic
    val getContext: Context
        get() {
            checkNotNull(ctx) { "context为空，请确保在Application.onCreate里面初始化了Constants.envInit" }
            return ctx!!
        }
}
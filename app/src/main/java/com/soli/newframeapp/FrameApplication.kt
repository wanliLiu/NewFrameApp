package com.soli.newframeapp

import com.soli.lib_common.BaseApplication
import com.soli.lib_common.base.Constant

/**
 * @author Soli
 * @Time 18-5-15 上午11:09
 */
class FrameApplication : BaseApplication() {

    override fun beforeLaunch() {
        Constant.Debug = BuildConfig.DEBUG
    }

    override fun onCreate() {
        super.onCreate()

    }
}
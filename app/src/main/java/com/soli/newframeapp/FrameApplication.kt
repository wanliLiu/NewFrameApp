package com.soli.newframeapp

import com.soli.libCommon.BaseApplication
import com.soli.libCommon.base.Constant

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
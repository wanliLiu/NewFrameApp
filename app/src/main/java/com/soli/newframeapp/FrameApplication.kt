package com.soli.newframeapp

import com.soli.libcommon.BaseApplication
import com.soli.libcommon.base.Constant

/**
 * @author Soli
 * @Time 18-5-15 上午11:09
 */
class FrameApplication : BaseApplication() {

    override fun beforeLaunch() {
        Constant.envInit(BuildConfig.DEBUG)
    }

    override fun onCreate() {
        super.onCreate()

    }
}
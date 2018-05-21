package com.soli.lib_common

import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.soli.lib_common.base.Constant
import com.soli.lib_common.util.FrescoUtil

/**
 * @author Soli
 * @Time 18-5-15 上午11:07
 */
abstract class BaseApplication : MultiDexApplication() {

    protected abstract fun beforeLaunch()

    override fun onCreate() {
        super.onCreate()

        Constant.init(this)

        beforeLaunch()

        if (Constant.Debug)
            Stetho.initializeWithDefaults(this)

        FrescoUtil.Init(this)
    }
}
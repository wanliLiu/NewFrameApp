package com.soli.libCommon

import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.soli.libCommon.base.Constant
import com.soli.libCommon.util.FrescoUtil

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
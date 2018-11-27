package com.soli.libCommon

import android.os.Build
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
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

        initSkin()

        if (Constant.Debug)
            Stetho.initializeWithDefaults(this)

        FrescoUtil.Init(this)
    }


    /**
     * 初始化皮肤框架
     * */
    private fun initSkin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)//适配android5.0以下
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}
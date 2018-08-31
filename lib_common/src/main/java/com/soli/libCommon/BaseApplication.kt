package com.soli.libCommon

import android.content.res.Configuration
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

        setDefaultConfig()

        Constant.init(this)

        beforeLaunch()

        if (Constant.Debug)
            Stetho.initializeWithDefaults(this)

        FrescoUtil.Init(this)
    }

    /**
     * 加载系统默认设置，字体不随用户设置变化
     */
    private fun setDefaultConfig(){
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
    }
}
package com.soli.libcommon

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.gu.toolargetool.TooLargeTool
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.FrescoUtil
import com.soli.libcommon.util.MLog
import io.reactivex.plugins.RxJavaPlugins
import me.yokeyword.fragmentation.Fragmentation

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

        if (Constant.Debug) {
            Stetho.initializeWithDefaults(this)
            TooLargeTool.startLogging(this)
        }


        FrescoUtil.Init(this)

        //Rxjava error handler  捕获Rxjava抛出的异常
        setRxJavaErrorHandler()

        initFragmentation()

    }

    /**
     * 初始化皮肤框架
     * */
    private fun initSkin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)//适配android5.0以下
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    /**
     * RxJava2 当取消订阅后(dispose())，RxJava抛出的异常后续无法接收(此时后台线程仍在跑，可能会抛出IO等异常),全部由RxJavaPlugin接收，需要提前设置ErrorHandler
     * 详情：http://engineering.rallyhealth.com/mobile/rxjava/reactive/2017/03/15/migrating-to-rxjava-2.html#Error Handling
     */
    private fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { MLog.e("Rxjava", it?.message ?: "Rxjava出现错误") }
    }

    /**
     * fragment框架
     */
    private fun initFragmentation() {
        Fragmentation.builder()
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(Constant.Debug)
            .handleException {
                MLog.e("fragment", it.message)
            }
            .install()
    }
}
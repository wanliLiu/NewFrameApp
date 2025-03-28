package com.soli.libcommon

//import com.gu.toolargetool.TooLargeTool
//import io.flutter.app.FlutterApplication
import android.os.Build
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.ActivityStackRecord
import com.soli.libcommon.util.FrescoUtil
import com.soli.libcommon.util.MLog
import com.soli.libcommon.util.ProcessName
import com.soli.libcommon.util.setCustomDensity
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import me.yokeyword.fragmentation.Fragmentation


/**
 * @author Soli
 * @Time 18-5-15 上午11:07
 */
abstract class BaseApplication : MultiDexApplication() {

    protected abstract fun beforeLaunch()

    override fun onCreate() {
        setCustomDensity()
        super.onCreate()

        Constant.init(this)
        ActivityStackRecord.init()

        beforeLaunch()

        FrescoUtil.Init(this)

        initForProcess()
    }


    /**
     * 根据进程来选择性初始化
     */
    private fun initForProcess() {
        val processName = ProcessName.getCurrentProcessName()
        setWebViewSuffix(processName)
        MLog.e("当前运行的进程", processName)
        when (processName) {
            ProcessName.MainProcess -> initForMainProcess()
            ProcessName.NimCoreProcess -> initForNimCoreProcess()
            ProcessName.JpusCoreProcess -> initForJpusCoreProcess()
        }
    }


    /**
     * 主进程初始
     */
    open fun initForMainProcess() {
        if (Constant.Debug) {
            Stetho.initializeWithDefaults(this)
//            TooLargeTool.startLogging(this)
        }

        initSkin()

        initFragmentation()

        //Rxjava error handler  捕获Rxjava抛出的异常
        setRxJavaErrorHandler()

        initLogger()
    }

    /**
     * 聊天进程需要的初始化
     */
    open fun initForNimCoreProcess() {

    }

    /**
     * 推送后台进程
     */
    open fun initForJpusCoreProcess() {

    }

    /**
     *Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
     */
    private fun setWebViewSuffix(processName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            MLog.e("setWebViewSuffix", "setWebViewSuffix: $processName")
            if (processName != packageName) {
                MLog.e("setWebViewSuffix", "setWebViewSuffix: $processName---real")
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }


    /**
     * 初始化皮肤框架
     * */
    private fun initSkin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)//适配android5.0以下
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    /**
     * rxjava3 当取消订阅后(dispose())，RxJava抛出的异常后续无法接收(此时后台线程仍在跑，可能会抛出IO等异常),全部由RxJavaPlugin接收，需要提前设置ErrorHandler
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


    /**
     *  初始化logger
     */
    private fun initLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(7) // (Optional) Hides internal method calls up to offset. Default 5
            .tag("NewFrameApp") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
    }
}
package com.soli.libcommon.view.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ZoomButtonsController
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.NetworkUtil
import com.soli.libcommon.util.ScreenWidth
import org.jsoup.Jsoup
import java.util.*

/**
 *
 *
 * Created by sofia on 4/28/2021.
 */
@SuppressLint("SetJavaScriptEnabled")
class BridgeWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = defStyle,
    defStyleRes: Int = defStyle
) : WebView(
    getFixedContext(context), attrs, defStyleAttr, defStyleRes
) {

    /**
     *
     */
    companion object {
        private val defStyle: Int
            private get() {
                try {
                    return Resources.getSystem().getIdentifier("webViewStyle", "attr", "android")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return 0
            }

        /**
         * 主要处理在Android 6.0以下 上有些手机WebView的崩溃问题，比如最多的就是vivo
         *
         * @param context
         * @return
         */
        private fun getFixedContext(context: Context): Context {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                context.createConfigurationContext(Configuration())
            else
                context
        }
    }


    private val mDetailImageList = ArrayList<String>()
    private var listener: OnWebImageClickListener? = null
    private var mClient: WebViewClient? = null

    init {
        init()
    }


    /**
     *
     */
    private fun init() {
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        disableControls()

        settings.apply {
            // 支持通过js打开新的窗口
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true

            //网页数据缓存
            cacheMode =
                if (NetworkUtil.isConnected()) WebSettings.LOAD_DEFAULT else WebSettings.LOAD_CACHE_ELSE_NETWORK // 缓存优先模式
            domStorageEnabled = true
            databaseEnabled = true
            databasePath = context.cacheDir.absolutePath + "/webwiew_dataBase"
            //H5缓存
            setAppCacheEnabled(true) // 开启缓存
            setAppCacheMaxSize((8 * 1024 * 1024).toLong()) // 设置最大缓存为8M
            setAppCachePath(context.cacheDir.absolutePath + "/H5Cache")
            setSupportMultipleWindows(true)
            useWideViewPort = true
            loadWithOverviewMode = true
            allowFileAccess = true
            lightTouchEnabled = true
            defaultTextEncodingName = "gbk"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setWebContentsDebuggingEnabled(true)
                //等页面finish后再发起图片加载
                loadsImagesAutomatically = true
            } else {
                loadsImagesAutomatically = false
            }

            //webview在安卓5.0之前默认允许其加载混合网络协议内容
            // 在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }

        webViewClient = generateBridgeWebViewClient()
        addJavascriptInterface(JavaScriptObject(), "injectedObject")
    }

    /**
     * 隐藏缩放按钮
     */
    private fun disableControls() {
        scrollBarStyle = SCROLLBARS_OUTSIDE_OVERLAY
        controlls
    }

    /**
     * This is where the magic happens :D
     */
    private val controlls: Unit
        get() {
            try {
                val webview = Class.forName("android.webkit.WebView")
                val method = webview.getMethod("getZoomButtonsController")
                method.invoke(this, true) as ZoomButtonsController
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    /**
     * @return
     */
    private fun generateBridgeWebViewClient(): BridgeWebViewClient {
        return BridgeWebViewClient(this, mClient, listener)
    }

    /**
     * @param client
     */
    fun setWebViewClientFromSide(client: WebViewClient?) {
        mClient = client
        webViewClient = generateBridgeWebViewClient()
    }

    /**
     *
     */
    private inner class JavaScriptObject {
        @JavascriptInterface
        fun openImage(url: String) {
            if (listener != null) {
                listener!!.onImageClick(findPosition(url), mDetailImageList)
            }
        }
    }

    /**
     * 把Html中的Img标签中的style设置为空
     *
     * @param html
     * @return
     */
    private fun dealAttr(html: String): String {
//        val doc = Jsoup.parse(html)
//        //图片
//        val es = doc.getElementsByTag("img")
//        for (e in es) {
//            val imgUrl = e.attr("src")
//            if (!TextUtils.isEmpty(imgUrl)) {
//                mDetailImageList.add(imgUrl)
//                val displayWidth: Int = Constant.getContext.ScreenWidth / 2
//                e.attr(
//                    "src",
//                    JokerImageUtil.INSTANCE.getRequestUrl(imgUrl, displayWidth, 0, true)
//                ) //+ ",h_" + displayHeight
//                MLog.d("多媒体加载html", e.attr("src"))
//                e.removeAttr("style")
//                e.removeAttr("width")
//                e.removeAttr("height")
//                e.attr("onclick", "openImage('$imgUrl')")
//            }
//        }
//
//        //<a>标签中的 target="_blank" 使用，如果是"_blank"，加前缀，然后打开手机本地浏览器，反之打开app内部浏览器
//        val a = doc.getElementsByTag("a")
//        for (el in a) {
//            el.attr("target", "_self")
//            val tem = el.attr("href")
//            if (!tem.startsWith("http")) el.attr("href", "http://$tem")
//        }
//        val iframe = doc.getElementsByTag("iframe")
//        for (el in iframe) {
//            val width = el.attr("width")
//            //            el.attr("width", getScreentWidth() + "");
//            try {
//                if (!TextUtils.isEmpty(width)) el.attr(
//                    "height",
//                    (Integer.valueOf(width) * 9 / 16).toString() + ""
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        return doc.html()

        return html
    }

    /**
     * 设置htlm文本
     *
     * @param html
     */
    fun setHtmlData(html: String) {
        loadDataWithBaseURL(null, dealAttr(html), "text/html", "utf-8", null)
    }

    /**
     * @return
     */
    private fun findPosition(url: String): Int {
        for (i in mDetailImageList.indices.reversed()) {
            if (mDetailImageList[i] == url) return i
        }
        return 0
    }

    /**
     * @param listener
     */
    fun setOnImageOpenListener(listener: OnWebImageClickListener?) {
        this.listener = listener
        webViewClient = generateBridgeWebViewClient()
    }
}
package com.soli.libcommon.view.web

import android.content.Context
import android.webkit.WebView
import java.io.*

/**
 * webView加载本地的js文件内容
 * <p>
 * Created by sofia on 4/28/2021.
 */
object BridgeUtil {

    private const val ENCODING = "utf-8"

    /**
     *
     * @param view
     * @param path
     */
    @JvmStatic
    fun webViewLoadLocalJs(view: WebView, path: String) {
        loadAssetsText(view.context, path)?.let {
            loadJs(view, it)
        }
    }


    /**
     *
     * @param view
     * @param js
     */
    @JvmStatic
    fun loadJs(view: WebView, js: String) {
        view.loadUrl("javascript:$js")
    }

    /**
     *
     */
    @JvmStatic
    fun loadAssetsText(context: Context, assetFielPath: String): String? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.resources.assets.open(assetFielPath)
            return convertStreamToString(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            inputStream?.close()
        }
        return null
    }

    @Throws(IOException::class)
    private fun convertStreamToString(inputStream: InputStream): String {
        val writer: Writer = StringWriter()
        val buffer = CharArray(2048)
        inputStream.use { input ->
            val reader: Reader = BufferedReader(InputStreamReader(input, ENCODING))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }
        return writer.toString()
    }
}
package com.soli.libcommon.view.web

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.text.TextUtils
import android.webkit.*
import com.soli.libcommon.util.MLog
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

/**
 *
 * Created by sofia on 4/28/2021.
 */
class BridgeWebViewClient(
    private val webView: BridgeWebView,
    private val mClient: WebViewClient?,
    private val listener: OnWebImageClickListener?
) : WebViewClient() {

    private val TAG = BridgeWebViewClient::class.java.simpleName

    private var mPrevUrl: String? = null

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, mUrl: String?): Boolean {
        view ?: return false
        mUrl ?: return false

        var url = mUrl

        val isDeal = mClient?.shouldOverrideUrlLoading(view, mUrl) ?: false

        if (isDeal)
            return true

        try {
            url = URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        MLog.d(TAG, "shouldOverrideUrlLoading-->$url")

        return dealOurOwn(view, url!!)
    }

    /**
     *
     * @param view
     * @param url
     * @return
     */
    private fun dealOurOwn(view: WebView, url: String): Boolean {
        if (mPrevUrl != null) {
            if (mPrevUrl != url) {
                if (!url.startsWith("http")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        view.context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return true
                } else {
                    mPrevUrl = url
                    if (listener != null) {
                        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                            listener.onUrlClick(url)
                            mPrevUrl = "haveDataLoad"
                            return true
                        }
                    } else {
                        return super.shouldOverrideUrlLoading(view, url)
                    }
                }
            } else {
                return false
            }
        } else {
            mPrevUrl = url
            if (listener != null) {
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    listener.onUrlClick(url)
                    mPrevUrl = "haveDataLoad"
                    return true
                }
            }
            return super.shouldOverrideUrlLoading(view, url)
        }
        return false
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        mClient?.onLoadResource(view, url)
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        mClient?.onPageCommitVisible(view, url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        mClient?.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        mClient?.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        mClient?.onPageFinished(view, url)
        super.onPageFinished(view, url)

        //等页面finish后再发起图片加
        if (!webView.settings.loadsImagesAutomatically) {
            webView.settings.loadsImagesAutomatically = true
        }

        BridgeUtil.webViewLoadLocalJs(view!!, "img_replace.js")
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        mClient?.onReceivedError(view, errorCode, description, failingUrl)
        super.onReceivedError(view, errorCode, description, failingUrl)
    }
}
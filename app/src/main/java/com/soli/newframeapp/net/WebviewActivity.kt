package com.soli.newframeapp.net

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.soli.libcommon.base.BaseActivity
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.activity_webview.*
import java.net.URLDecoder

/**
 * @author Soli
 * @Time 18-7-19 下午2:12
 */
class WebviewActivity : BaseActivity() {

    private val TAG = WebviewActivity::class.java.simpleName

    override fun getContentView() = R.layout.activity_webview

    override fun initView() {
        title = "WebView"
    }

    override fun initListener() {
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                rootView.getToolbar()?.setLoadingProgress(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, mtitle: String?) {
                super.onReceivedTitle(view, mtitle)
                title = mtitle ?: "WebView"
            }
        }
        mWebView.setWebViewClientFromSide(object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                rootView.getToolbar()?.showLoadingProgress(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                rootView.getToolbar()?.showLoadingProgress(false)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.e(TAG, "shouldOverrideUrlLoading-->${URLDecoder.decode(url, "UTF-8")}")
                return false
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                Log.e(TAG, "onLoadResource-->${URLDecoder.decode(url, "UTF-8")}")
            }
        })
    }

    override fun initData() {
        mWebView.loadUrl("https://m.bilibili.com/")
    }

    override fun onBackPressedSupport() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack() // goBack()表示返回WebView的上一页面
        } else {
            finish()
        }
    }
}
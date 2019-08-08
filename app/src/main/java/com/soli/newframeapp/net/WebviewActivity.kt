package com.soli.newframeapp.net

import android.webkit.WebView
import android.webkit.WebViewClient
import com.soli.libcommon.base.BaseActivity
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.activity_webview.*

/**
 * @author Soli
 * @Time 18-7-19 下午2:12
 */
class WebviewActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_webview

    override fun initView() {
        title = "WebView"

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
    }

    override fun initListener() {
    }

    override fun initData() {

        webView.loadUrl("https://www.baidu.com")//https://wap.showstart.com/venue/it/426166
    }
}
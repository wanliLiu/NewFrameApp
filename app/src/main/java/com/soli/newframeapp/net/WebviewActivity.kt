package com.soli.newframeapp.net

import android.graphics.Bitmap
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.util.dimens
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.ActivityWebviewBinding
import java.net.URLDecoder

/**
 * @author Soli
 * @Time 18-7-19 下午2:12
 */
class WebviewActivity : BaseActivity<ActivityWebviewBinding>() {

    private val TAG = WebviewActivity::class.java.simpleName

    private val DefaultLoadUrl = "https://m.bilibili.com/"

    override fun initView() {

        rootView.getToolbar()
            ?.inSearchModel(
                needClick = true,
                needClear = false,
                params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, ctx.dimens(R.dimen.toolbar_height)
                ).apply {
                    leftMargin = ctx.dimens(R.dimen.toolbar_height) / 2
                    rightMargin = leftMargin
                }
            ) { content, clickSearch ->
                if (clickSearch) {
                    goToWebView(content)
                }
            }?.apply {
                hideFinishButtom()
                setInputText(DefaultLoadUrl)
            }
    }

    override fun initListener() {
        binding.mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                rootView.getToolbar()?.setLoadingProgress(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, mtitle: String?) {
                super.onReceivedTitle(view, mtitle)
//                title = mtitle ?: "WebView"
            }
        }
        binding.mWebView.setWebViewClientFromSide(object : WebViewClient() {
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

                val includeVideo = url!!.indexOf(".mp4") != -1 || url.indexOf(".m3u8") != -1
                if (includeVideo) {
                    Log.e(
                        "captureVideo",
                        "$url \n cookie : ${CookieManager.getInstance().getCookie(url)}"
                    )
                }
                Log.e("webViewTargetUrl", view!!.url.toString())
                Log.e(
                    TAG,
                    "onLoadResource-->${
                        URLDecoder.decode(
                            url,
                            "UTF-8"
                        )
                    } \n cookie : ${CookieManager.getInstance().getCookie(url)}"
                )
            }
        })
    }

    override fun initData() {
        goToWebView(DefaultLoadUrl)
    }

    /**
     *
     */
    private fun goToWebView(url: String) {
        binding.mWebView.loadUrl(if (!url.startsWith("http")) "http://$url" else url)
    }

    override fun onBackPressedSupport() {
        if (binding.mWebView != null && binding.mWebView.canGoBack()) {
            binding.mWebView.goBack() // goBack()表示返回WebView的上一页面
        } else {
            finish()
        }
    }
}
package com.soli.newframeapp

import android.widget.ScrollView
import com.jakewharton.rxbinding4.view.clicks
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.net.apiParamsOf
import com.soli.libcommon.net.websocket.RxWebSocket
import com.soli.newframeapp.databinding.ActivityWebsocketBinding

/**
 *
 * @author Soli
 * @Time 2018/10/8 17:19
 */

class WebsocketActivity : BaseActivity<ActivityWebsocketBinding>() {

    //    TODO Echo test地址：http://www.websocket.org/echo.html
    private var url: String = "ws://echo.websocket.org"

    override fun initView() {
        title = "WebSocket"
    }

    override fun initListener() {

        val disposable = binding.btnSend.clicks()
//            .compose(RxLifecycle.with(this).bindToLifecycle())
            .subscribe {
                val str = binding.msgSend.text.toString()
                sendThrowWebSocket(msg = str)
            }
    }

    override fun initData() {

//        Schedulers.io().createWorker().schedule { this.initLocalServerWebsocket() }
        binding.msgBack.text = "创建本地Websocket服务:$url\n"
        RxWebSocket.Instance.keepOnline()
    }

    private fun sendThrowWebSocket(isWebSocket: Boolean = true, msg: String) {

//        showProgress(LoadingType.TypeDialog)

//        val params = ApiParams().apply {
//            put("param", "速度速度上来看到了的抗衰老的 -----索德罗斯；的是生理上的历史速度速度上来看到了的抗衰老的 -----索德罗斯")
//            getWebSocketParams("/security/check")
//        }
//        RxWebSocket.Instance.asyncSend(params.jsonParams)

        ApiHelper.build {
            url = "/security/check"
            params = apiParamsOf("param" to msg)
            isWebSocketRequest = isWebSocket
        }.request<String> {
//            if (it.isSuccess){
//
//            }

            binding.msgBack.append("${it.result as String}\n")
            scrollToBottom()
        }
    }

    /**
     *
     */
    private fun scrollToBottom() {
        binding.msgBack.post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        mDisposable?.dispose()
        RxWebSocket.Instance.release()
    }
}
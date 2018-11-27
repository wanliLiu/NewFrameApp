package com.soli.newframeapp

import android.widget.ScrollView
import com.dhh.rxlifecycle2.RxLifecycle
import com.jakewharton.rxbinding2.view.RxView
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.ApiHelper
import com.soli.libCommon.net.ApiParams
import com.soli.libCommon.net.websocket.RxWebSocket
import kotlinx.android.synthetic.main.activity_websocket.*

/**
 *
 * @author Soli
 * @Time 2018/10/8 17:19
 */

class WebsocketActivity : BaseActivity() {

    private var url: String = "wss://demos.kaazing.com/echo"

    override fun getContentView() = R.layout.activity_websocket

    override fun initView() {
        title = "WebSocket"
    }

    override fun initListener() {

        val disposable = RxView.clicks(btnSend)
            .compose(RxLifecycle.with(this).bindToLifecycle())
            .subscribe {
                val str = msgSend.text.toString()
                sendThrowWebSocket(msg = str)
            }
    }

    override fun initData() {

//        Schedulers.io().createWorker().schedule { this.initLocalServerWebsocket() }
        msgBack.text = "创建本地Websocket服务:$url\n"
        RxWebSocket.Instance.keepOnline()
    }

    private fun sendThrowWebSocket(isWebSocket: Boolean = true, msg: String) {

//        showProgress(LoadingType.TypeDialog)

//        val params = ApiParams().apply {
//            put("param", "速度速度上来看到了的抗衰老的 -----索德罗斯；的是生理上的历史速度速度上来看到了的抗衰老的 -----索德罗斯")
//            getWebSocketParams("/security/check")
//        }
//        RxWebSocket.Instance.asyncSend(params.jsonParams)

        val params = ApiParams().apply {
            put("param", msg)
        }

        val request = ApiHelper.Builder()
            .url("/security/check")
            .params(params)

        (if (isWebSocket) request.webSocket() else request.build()).request { result ->
            //            dismissProgress()
//            if (result.isSuccess) {
            msgBack.append("${result.result as String}\n")
            scrollToBottom()
//                Toast.makeText(ctx as Context, result.result as String, Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(ctx as Context, result.errormsg, Toast.LENGTH_SHORT).show()
//            }
        }
    }

    /**
     *
     */
    private fun scrollToBottom() {
        msgBack.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        mDisposable?.dispose()
        RxWebSocket.Instance.release()
    }
}
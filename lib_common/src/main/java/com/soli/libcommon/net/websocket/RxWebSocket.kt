package com.soli.libcommon.net.websocket

import com.soli.libcommon.net.ApiHelper
import com.soli.libcommon.util.MLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * @author Soli
 * @Time 2018/11/8 11:25
 */
class RxWebSocket private constructor() {

    companion object {
        val Instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { RxWebSocket() }
    }

    private val logTag = "RxWebSocket"

    private val client = ApiHelper.getHttpClient().newBuilder().pingInterval(5, TimeUnit.SECONDS).build()

    private var observableWe: Observable<WebSocketInfo>? = null
    private var webSocketClient: WebSocket? = null

    //重试等等的时间  单位秒
    private val retryTime = 2L

    private var mDisposable: Disposable? = null

    /**
     * @return
     */
    //共享
    fun getWebSocketInfoObservable(): Observable<WebSocketInfo> {
        if (observableWe == null) {
            observableWe = Observable.create(WebSocketInfoOnSubscribe())
                .retryWhen(awlaysRetry())
                .doOnDispose {
                    observableWe = null
                    webSocketClient?.apply {
                        this.close(3000, "自然退出 关闭WebSocket")
                    }
                    webSocketClient = null
                }
                .doOnNext { webSocketInfo ->
                    webSocketClient = webSocketInfo.webSocket
                }
                .share()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        } else {
            webSocketClient?.apply {
                observableWe = observableWe!!.startWith(WebSocketInfo(this))
            }
        }
        return observableWe!!.observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * @return
     */
    //fix #31
    private val webSocket: Observable<WebSocket>
        get() = getWebSocketInfoObservable()
            .filter { webSocketInfo -> webSocketInfo.webSocket != null }
            .map { webSocketInfo -> webSocketInfo.webSocket }


    /**
     * 只要有错误就一直重试
     */
    private inner class awlaysRetry : Function<Observable<out Throwable>, Observable<*>> {
        override fun apply(attempts: Observable<out Throwable>): Observable<*> {
            return attempts.flatMap { throwable ->
                MLog.e(logTag, "出现异常，重新创建Websocket --- " + throwable.message)
                Observable.timer(retryTime, TimeUnit.SECONDS)
            }
        }
    }


    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param msg
     */
    fun asyncSend(msg: String) {
        val temp = webSocket
            .take(1)
            .subscribe { webSocket -> webSocket.send(msg) }
    }

    /**
     * 获取websocket 需要连接的地址
     */
    private inner class WebSocketInfoOnSubscribe : ObservableOnSubscribe<WebSocketInfo> {

        override fun subscribe(emitter: ObservableEmitter<WebSocketInfo>) {
            if (!emitter.isDisposed) {
                initWebSocket(emitter)
            }
        }

        private fun getRequest(url: String): Request {
            return Request.Builder().get().url(url).build()
        }

        /**
         * "wss://demos.kaazing.com/echo"
         *
         * @param webSocketUrl
         * @param emitter
         */
        private fun creatWebSocket(webSocketUrl: String, emitter: ObservableEmitter<WebSocketInfo>) {
            MLog.e(logTag, "开始连接：$webSocketUrl")
            client.newWebSocket(getRequest(webSocketUrl), object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket?, response: Response?) {
                    MLog.e(logTag, "链接服务器: $webSocketUrl 成功")
                    webSocketClient = webSocket
                    emitter.onNext(WebSocketInfo(webSocket))
                    emitter.onNext(WebSocketInfo(webSocket,"链接服务器: $webSocketUrl 成功"))
                }

                override fun onMessage(webSocket: WebSocket?, text: String?) {
                    emitter.onNext(WebSocketInfo(webSocket, text))
                }

                override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                    emitter.onError(Exception(t))
                }

                override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                    webSocket!!.close(code, reason)
                }

                override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                    MLog.d(logTag, "$webSocketUrl --> onClosed:code= $code--> reason:$reason")
                }
            })
        }

        /**
         * @param emitter
         */
        private fun initWebSocket(emitter: ObservableEmitter<WebSocketInfo>) {
            creatWebSocket("wss://demos.kaazing.com/echo", emitter)
//            val params = ApiParams()
//            params["token"] = AuthInfo.getToken()
//            ApiHelper.Builder()
//                .url(Constant.WebSocketPath)
//                .params(params)
//                .build()
//                .request { result ->
//                    if (result.isSuccess) {
//                        val json = JSON.parseObject(result.fullData)
//                        val data = json.getString("data")
//                        if (!TextUtils.isEmpty(data)) {
//                            creatWebSocket(data, emitter)
//                        } else
//                            emitter.onError(Exception("获取WebSocket Url 为空"))
//
//                    } else {
//                        emitter.onError(Exception("获取WebSocket Url 接口出错：---------${result.errormsg}"))
//                    }
//                }
        }
    }

    /**
     * 建立连接并维护
     */
    fun keepOnline() {
        mDisposable = getWebSocketInfoObservable()
            .filter { webSocketInfo -> webSocketInfo.string != null }
            .subscribe { webSocketInfo ->
                val result = webSocketInfo.string
                if (result != null) {
                    //FIXME 服务端主动下发处理，估计到时候根据token特定值来做 这里没有处理服务器主动下发的数据
                    MLog.e(logTag, "$result")
//                    JSON.parseObject(result)?.apply {
//                        val token = getString("token")
//                        val code = getString("code")
//                        if (code == "0" && !TextUtils.isEmpty(token)) {
////                            setResult(token, getString("content"))
//                        }
//                    }
                }
            }
    }

    /**
     *  退出app 关闭连接
     */
    fun release() {
        mDisposable?.apply {
            dispose()
        }
    }
}

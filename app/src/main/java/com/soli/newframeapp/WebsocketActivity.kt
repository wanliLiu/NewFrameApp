package com.soli.newframeapp

import android.text.TextUtils
import android.util.Log
import android.widget.ScrollView
import com.dhh.rxlifecycle2.RxLifecycle
import com.jakewharton.rxbinding2.view.RxView
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.net.websocket.RxWebSocket
import com.soli.libCommon.util.RxJavaUtil
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_websocket.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.ByteString

/**
 *
 * @author Soli
 * @Time 2018/10/8 17:19
 */

class WebsocketActivity : BaseActivity() {

    private var url: String = "wss://demos.kaazing.com/echo"
    private var mWebSocket: WebSocket? = null
    private var mDisposable: Disposable? = null

    override fun getContentView() = R.layout.activity_websocket

    override fun initView() {
        title = "WebSocket"
    }

    override fun initListener() {
//        btnSend.setOnClickListener {
//            val str = msgSend.text.toString()
//            if (mWebSocket != null && !TextUtils.isEmpty(str)) {
//                mWebSocket!!.send(str)
//            } else {
//                send()
//            }
//        }

        val disposable = RxView.clicks(btnSend)
                .compose(RxLifecycle.with(this).bindToLifecycle())
                .subscribe {
                    val str = msgSend.text.toString()
                    if (mWebSocket != null && !TextUtils.isEmpty(str)) {
                        mWebSocket!!.send(str)
                    } else {
                        send()
                    }
                }
    }

    override fun initData() {

//        Schedulers.io().createWorker().schedule { this.initLocalServerWebsocket() }
        msgBack.text = "创建本地Websocket服务:$url\n"

        RxJavaUtil.delayAction(500) { connect() }
    }

    /**
     *
     */
    private fun connect() {
        if (mDisposable != null) return
        //注意取消订阅,有多种方式,比如 rxlifecycle
        mDisposable = RxWebSocket.get(url)
                // RxLifeCycle: https://github.com/dhhAndroid/RxLifecycle
                .compose(RxLifecycle.with(this).bindOnDestroy())
                .subscribe { webSocketInfo ->
                    mWebSocket = webSocketInfo.webSocket
                    if (webSocketInfo.isOnOpen) {
                        msgBack.append("链接服务器:${url}成功\n")
                        Log.e("MainActivity", " on WebSocket open")
                    } else if (webSocketInfo.isOnReconnect) {
                        msgBack.append("---主动断开，然后重新链接服务器\n")
                    } else {
                        val string = webSocketInfo.string
                        if (string != null) {
                            Log.e("MainActivity", string)
                            msgBack.append("$string\n")
                        }

                        val byteString = webSocketInfo.byteString
                        if (byteString != null) {
                            Log.e("MainActivity", "webSocketInfo.getByteString():$byteString")
                        }
                    }
                    scrollToBottom()
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

    /**
     *
     */
    fun send() {
        //引用直接发
        mWebSocket!!.send("hello")
        //url 对应的WebSocket 必须打开,否则报错
        RxWebSocket.send(url, "hello")
        RxWebSocket.send(url, ByteString.EMPTY)
        //异步发送,若WebSocket已经打开,直接发送,若没有打开,打开一个WebSocket发送完数据,直接关闭.
        RxWebSocket.asyncSend(url, "hello")
        RxWebSocket.asyncSend(url, ByteString.EMPTY)
    }

    /**
     *
     */
    private fun initLocalServerWebsocket() {
        val mockWebServer = MockWebServer()
        url = "ws://" + mockWebServer.hostName + ":" + mockWebServer.port + "/"
        RxJavaUtil.runOnUiThread {
            msgBack.text = "创建本地Websocket服务:$url\n"
        }
        mockWebServer.enqueue(MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send("hello, I am  websocket server !")
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                Log.e("MainActivity", "收到客户端消息:" + text!!)
                webSocket!!.send("Server response:$text")
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {

            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {}
        }))
    }

    private fun demo() {
//        RxWebSocket.get(url)
//                //RxLifecycle : https://github.com/dhhAndroid/RxLifecycle
//                .compose(RxLifecycle.with(this).bindToLifecycle())
//                .subscribe(object : WebSocketSubscriber() {
//                    override fun onOpen(@NonNull webSocket: WebSocket) {
//                        Log.d("MainActivity", "onOpen1:")
//                    }
//
//                    override fun onMessage(@NonNull text: String) {
//                        Log.d("MainActivity", text)
//                    }
//
//                    override fun onMessage(@NonNull bytes: ByteString) {
//
//                    }
//
//                    override protected fun onReconnect() {
//                        Log.d("MainActivity", "重连")
//                    }
//
//                    override  protected fun onClose() {
//
//                    }
//
//                    override fun onError(e: Throwable) {
//
//                    }
//                })


//               RxWebSocket.get("ws://10.7.5.88:8089/status")
//                .subscribe(new WebSocketSubscriber() {
//                    @Override
//                    protected void onMessage(String text) {
//
//                    }
//
//                    @Override
//                    protected void onReconnect() {
//                        Log.d("MainActivity", "重连");
//                    }
//                });


        /**
         *
         *如果你想将String类型的text解析成具体的实体类，比如{@link List<String>},
         * 请使用 {@link  WebSocketSubscriber2}，仅需要将泛型传入即可
         */
//        RxWebSocket.get("your url")
//                .compose(RxLifecycle.with(this).<WebSocketInfo>bindToLifecycle())
//                .subscribe(new WebSocketSubscriber2<List<String>>() {
//                    @Override
//                    protected void onMessage(List<String> strings) {
//
//                    }
//                });

        //注销
//        Disposable disposable = RxWebSocket.get("ws://sdfs").subscribe();
//        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
//        }

    }

    override fun onResume() {
        super.onResume()

        //        RxWebSocket.get("url")
        //                .compose(RxLifecycle.with(this).<WebSocketInfo>bindToLifecycle())
        //                .subscribe(new WebSocketSubscriber() {
        //                    @Override
        //                    protected void onMessage(@NonNull String text) {
        //
        //                    }
        //                });

        //        RxWebSocket.get("your url")
        //                //RxLifecycle : https://github.com/dhhAndroid/RxLifecycle
        //                .compose(RxLifecycle.with(this).<WebSocketInfo>bindToLifecycle())
        //                .subscribe(new WebSocketSubscriber() {
        //                    @Override
        //                    public void onOpen(@NonNull WebSocket webSocket) {
        //                        Log.d("MainActivity", "onOpen1:");
        //                    }
        //
        //                    @Override
        //                    public void onMessage(@NonNull String text) {
        //                        Log.d("MainActivity", "返回数据:" + text);
        //                    }
        //
        //                    @Override
        //                    public void onMessage(@NonNull ByteString byteString) {
        //
        //                    }
        //
        //                    @Override
        //                    protected void onReconnect() {
        //                        Log.d("MainActivity", "重连:");
        //                    }
        //
        //                    @Override
        //                    protected void onClose() {
        //                        Log.d("MainActivity", "onClose:");
        //                    }
        //                });
    }


    override fun onDestroy() {
        super.onDestroy()
//        mDisposable?.dispose()
    }
}
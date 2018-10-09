package com.soli.libCommon.net.websocket;

import android.os.SystemClock;

import com.soli.libCommon.util.MLog;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


/**
 * Created by dhh on 2017/9/21.
 * <p>
 * WebSocketUtil based on okhttp and RxJava
 * </p>
 * Core Feature : WebSocket will be auto reconnection onFailed.
 */
public class RxWebSocketUtil {
    private static RxWebSocketUtil instance;

    private OkHttpClient client;

    private Map<String, Observable<WebSocketInfo>> observableMap;
    private Map<String, WebSocket> webSocketMap;
    private String logTag = "RxWebSocket";
    private long interval = 2;
    private TimeUnit reconnectIntervalTimeUnit = TimeUnit.SECONDS;

    private RxWebSocketUtil() {
        try {
            Class.forName("okhttp3.OkHttpClient");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Must be dependency okhttp3 !");
        }
        try {
            Class.forName("io.reactivex.Observable");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Must be dependency rxjava 2.x");
        }
        try {
            Class.forName("io.reactivex.android.schedulers.AndroidSchedulers");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Must be dependency rxandroid 2.x");
        }
        observableMap = new ConcurrentHashMap<>();
        webSocketMap = new ConcurrentHashMap<>();
        client = new OkHttpClient();
    }

    /**
     * please use {@link RxWebSocket} to instead of it
     *
     * @return
     */
    public static RxWebSocketUtil getInstance() {
        if (instance == null) {
            synchronized (RxWebSocketUtil.class) {
                if (instance == null) {
                    instance = new RxWebSocketUtil();
                }
            }
        }
        return instance;
    }

    /**
     * set your client
     *
     * @param client
     */
    public void setClient(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException(" Are you kidding me ? client == null");
        }
        this.client = client;
    }

    /**
     * 设置出错的时候重连时间
     * @param interval
     * @param timeUnit
     */
    public void setReconnectInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.reconnectIntervalTimeUnit = timeUnit;

    }

    /**
     * @param url      ws://127.0.0.1:8080/websocket
     * @param timeout  The WebSocket will be reconnected after the specified time interval is not "onMessage",
     *                 <p>
     *                 在指定时间间隔后没有收到消息就会重连WebSocket,为了适配小米平板,因为小米平板断网后,不会发送错误通知
     * @param timeUnit unit
     * @return
     */
    public Observable<WebSocketInfo> getWebSocketInfo(final String url, final long timeout, final TimeUnit timeUnit) {
        Observable<WebSocketInfo> observable = observableMap.get(url);
        if (observable == null) {
            observable = Observable.create(new WebSocketOnSubscribe(url))
                    //一定时间没有onext就自动断开了，然后重新链接
                    .timeout(timeout, timeUnit)
                    .retry(throwable -> throwable instanceof IOException || throwable instanceof TimeoutException)
                    //共享
                    .doOnDispose(() -> {
                        observableMap.remove(url);
                        webSocketMap.remove(url);
                        MLog.d(logTag, "OnDispose");
                    })
                    .doOnNext(webSocketInfo -> {
                        if (webSocketInfo.isOnOpen()) {
                            webSocketMap.put(url, webSocketInfo.getWebSocket());
                        }
                    })
                    .share()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observableMap.put(url, observable);
        } else {
            WebSocket webSocket = webSocketMap.get(url);
            if (webSocket != null) {
                observable = observable.startWith(new WebSocketInfo(webSocket, true));
            }
        }
        return observable.observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * default timeout: 30 days
     * <p>
     * 若忽略小米平板,请调用这个方法
     * </p>
     */
    public Observable<WebSocketInfo> getWebSocketInfo(String url) {
        return getWebSocketInfo(url, 30, TimeUnit.DAYS);
    }

    /**
     * @param url
     * @return
     */
    public Observable<String> getWebSocketString(String url) {
        return getWebSocketInfo(url)
                .filter(webSocketInfo -> webSocketInfo.getString() != null)
                .map(webSocketInfo -> webSocketInfo.getString());
    }

    /**
     * @param url
     * @return
     */
    public Observable<ByteString> getWebSocketByteString(String url) {
        return getWebSocketInfo(url)
                .filter(webSocketInfo -> webSocketInfo.getByteString() != null)
                .map(webSocketInfo -> webSocketInfo.getByteString());
    }

    /**
     * @param url
     * @return
     */
    public Observable<WebSocket> getWebSocket(String url) {
        return getWebSocketInfo(url)
                //fix #31
                .filter(webSocketInfo -> webSocketInfo.getWebSocket() != null)
                .map(webSocketInfo -> webSocketInfo.getWebSocket());
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param msg
     */
    public void send(String url, String msg) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(msg);
        } else {
            throw new IllegalStateException("The WebSokcet not open");
        }
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param byteString
     */
    public void send(String url, ByteString byteString) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(byteString);
        } else {
            throw new IllegalStateException("The WebSokcet not open");
        }
    }

    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param url
     * @param msg
     */
    public void asyncSend(String url, final String msg) {
        getWebSocket(url)
                .take(1)
                .subscribe(webSocket -> webSocket.send(msg));

    }

    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param url
     * @param byteString
     */
    public void asyncSend(String url, final ByteString byteString) {
        getWebSocket(url)
                .take(1)
                .subscribe(webSocket -> webSocket.send(byteString));
    }

    private Request getRequest(String url) {
        return new Request.Builder().get().url(url).build();
    }

    /**
     *
     */
    private final class WebSocketOnSubscribe implements ObservableOnSubscribe<WebSocketInfo> {
        private String url;

        private WebSocket webSocket;

        public WebSocketOnSubscribe(String url) {
            this.url = url;
        }

        @Override
        public void subscribe(@NonNull ObservableEmitter<WebSocketInfo> emitter) throws Exception {
            if (webSocket != null) {
                //降低重连频率
                if (!"main".equals(Thread.currentThread().getName())) {
                    long ms = reconnectIntervalTimeUnit.toMillis(interval);
                    if (ms == 0) {
                        ms = 1000;
                    }
                    SystemClock.sleep(ms);
                    emitter.onNext(WebSocketInfo.createReconnect());
                }
            }
            initWebSocket(emitter);
        }

        private void initWebSocket(final ObservableEmitter<WebSocketInfo> emitter) {
            webSocket = client.newWebSocket(getRequest(url), new WebSocketListener() {
                @Override
                public void onOpen(final WebSocket webSocket, Response response) {
                    MLog.d(logTag, url + " --> onOpen");
                    webSocketMap.put(url, webSocket);
                    if (!emitter.isDisposed()) {
                        emitter.onNext(new WebSocketInfo(webSocket, true));
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(new WebSocketInfo(webSocket, text));
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(new WebSocketInfo(webSocket, bytes));
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    MLog.e(logTag, t.toString() + webSocket.request().url().uri().getPath());
                    if (!emitter.isDisposed()) {
                        emitter.onError(t);
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(1000, null);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    MLog.d(logTag, url + " --> onClosed:code= " + code + " --> reason:" + reason);
                }
            });
            emitter.setCancellable(() -> {
                webSocket.close(3000, "close WebSocket");
                MLog.d(logTag, url + " --> cancel ");
            });
        }
    }
}

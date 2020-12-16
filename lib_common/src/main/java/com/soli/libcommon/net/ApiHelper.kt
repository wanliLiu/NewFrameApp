package com.soli.libcommon.net;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.soli.libcommon.base.Constant;
import com.soli.libcommon.net.cookie.https.HttpsUtils;
import com.soli.libcommon.net.download.FileProgressListener;
import com.soli.libcommon.net.download.ProgressInterceptor;
import com.soli.libcommon.net.upload.ProgressRequestBody;
import com.soli.libcommon.net.websocket.RxWebSocket;
import com.soli.libcommon.util.FileUtil;
import com.soli.libcommon.util.NetworkUtil;
import com.soli.libcommon.util.RxJavaUtil;
import com.soli.libcommon.util.Utils;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.LoggingEventListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Soli
 * @Time 18-5-17 下午4:42
 */
public class ApiHelper {

    private static OkHttpClient okHttpClient;

    private long timeout = 30L;
    private Retrofit retrofit;
    private static final Map<String, Call> CALL_MAP = new HashMap<>();
    private Builder mBuilder;
    private static ApiHelper client;


    private boolean isWebSocketRequest = false;

    //文件下载进度回调
    private FileProgressListener progressListener;

    /**
     * 获取httpClient供Fresco使用
     *
     * @return
     */
    public static OkHttpClient getHttpClient() {
        return getInstance().getOkHttpClient();
    }

    /**
     * 获取单例
     *
     * @return
     */
    private static ApiHelper getInstance() {
        if (client != null)
            return client;
        synchronized (ApiHelper.class) {
            if (client == null)
                client = new ApiHelper();
        }

        return client;
    }

    /**
     *
     */
    private ApiHelper() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(timeout, TimeUnit.SECONDS);
        client.readTimeout(timeout, TimeUnit.SECONDS);
        client.writeTimeout(timeout, TimeUnit.SECONDS);
        client.retryOnConnectionFailure(true);
//        client.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Constant.getContext())));
        client.hostnameVerifier((hostname, session) -> true);

        addProgress(client);
        netWorkCacheSet(client);

        //添加公共请求头的参数
        client.addInterceptor(new RequestHeaderInterceptor());

        if (Constant.Debug) {
            client.addInterceptor((new HttpLoggingInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY));
            client.eventListenerFactory(new LoggingEventListener.Factory());
            //for stetho 在网页调试页看网络日志
//            client.addNetworkInterceptor(new StethoInterceptor());
        }

        //支持https访问  Android 5.0以下 TLSV1.1和TLSV1.2是关闭的，要自己打开，Android 5.0以上是打开的
        //这里就针对这两种情况，不同处理
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
        //Android 5.0以上
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        client.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        } else {
//            //Android 5.0 以下
//            SSLContext sslContext = null;
//            try {
//                sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, null, null);
//
//                SSLSocketFactory socketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());
//                client.sslSocketFactory(socketFactory, new HttpsUtils.UnSafeTrustManager());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        okHttpClient = client.build();
    }

    /**
     * 网络缓存设置
     *
     * @param builder
     */
    private void netWorkCacheSet(OkHttpClient.Builder builder) {
        File mFile = new File(Constant.getContext().getCacheDir() + "http");//储存目录
        long maxSize = 10 * 1024 * 1024; // 10 MB 最大缓存数
        builder.cache(new Cache(mFile, maxSize));

        builder.interceptors().add(new CacheInterceptor());
        builder.networkInterceptors().add(new CacheInterceptor());
    }

    /**
     * 添加对网络数据获取的进度添加
     *
     * @param builder
     */
    private void addProgress(OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(new ProgressInterceptor((progress, bytesRead, updateBytes, fileSize, done) -> {
            if (progressListener != null) {
                RxJavaUtil.runOnUiThread(() -> {
                    if (progressListener != null)
                        progressListener.progress(progress, bytesRead, updateBytes, fileSize, done);
                });
            }
            return null;
        }));
    }

    /**
     * 获取的Retrofit的实例，
     * 引起Retrofit变化的因素只有静态变量BASE_URL的改变。
     */
    private void getRetrofit() {
        //默认的
        String defaultUrl = Constant.webServer;
        //动态设置的
        String setUrl = mBuilder.builderBaseUrl;
        boolean isNeedGet = false;
        if (retrofit == null)
            isNeedGet = true;
        else if (!TextUtils.isEmpty(setUrl) && !defaultUrl.equals(setUrl))
            isNeedGet = true;
        else if (!retrofit.baseUrl().toString().equals(defaultUrl))
            isNeedGet = true;

        if (isNeedGet) {
            if (TextUtils.isEmpty(setUrl))
                setUrl = defaultUrl;
            retrofit = new Retrofit.Builder()
                    .baseUrl(setUrl)
                    .client(okHttpClient)
                    .build();
        }
    }

    /**
     * @return
     */
    private OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 发送get请求
     *
     * @param callBack
     */
    public void get(final ApiCallBack callBack) {
        Builder builder = mBuilder;
        builder.url(builder.url + "?" + builder.params.getParams());
        Call<ResponseBody> mCall = retrofit.create(ApiService.class).executeGet(builder.url);
        putCall(builder, mCall);
        startRequest(builder, callBack, mCall);
    }

    /**
     * 发送post请求
     *
     * @param callBack
     */
    public void post(final ApiCallBack callBack) {
        Builder builder = mBuilder;
        Call<ResponseBody> mCall = retrofit.create(ApiService.class).executePost(builder.url, builder.params);
        putCall(builder, mCall);
        startRequest(builder, callBack, mCall);
    }


    /**
     * 发送post请求
     *
     * @param callBack
     */
    public void request(final ApiCallBack callBack) {
        if (!isWebSocketRequest)
            post(callBack);
        else
            webSocketRequest(callBack);
    }

    /**
     * 监听websocket回来的数据并处理
     *
     * @param builder
     * @param callBack
     */
    private void listenerWebSocketResult(Builder builder, final ApiCallBack callBack) {
        Disposable disposable = RxWebSocket.Companion.getInstance().getWebSocketInfoObservable()
                .timeout(10, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .filter(webSocketInfo -> webSocketInfo.getString() != null)
                .take(1)
                .subscribe(dataBack -> {
                    ApiResult result;
                    try {
                        result = new ApiResult(ResultCode.RESULT_OK, parseData(dataBack.getString(), builder.clazz, builder.bodyType), dataBack.getString());
//                        JSONObject json = JSON.parseObject(dataBack.getString());
//                        String token = json.getString("token");
//                        String code = json.getString("code");
//                        String content = json.getString("content");
//
//                        if (!token.equals(builder.params.get("token").toString()))
//                            //回来的和请求的token不一致，就不处理，继续等待
//                            return;
//
//                        boolean isOkay = false;
//                        if (code.equals("0") &&
//                                !TextUtils.isEmpty(token)
//                                && !TextUtils.isEmpty(content)) {
//                            isOkay = true;
//                        }
//
//                        if (isOkay) {
//                            result = parseOriginData(builder, content);
//                        } else {
//                            result = new ApiResult(ResultCode.RESULT_FAILED, "websocket返回的数据有问题");
//                        }
                    } catch (Exception e) {
                        result = new ApiResult(ResultCode.RESULT_FAILED, e.getMessage());
                    }

                    if (callBack != null) {
                        callBack.receive(result);
                    }

                }, throwable -> {
                    if (callBack != null) {
                        callBack.receive(new ApiResult(ResultCode.RESULT_FAILED, throwable.getMessage()));
                    }
                });
    }

    /**
     * 每个请求的超时时间为10s,就是如果10s,对应的token数据都还没回来，那么就timeoutException
     *
     * @param callBack
     */
    private void webSocketRequest(final ApiCallBack callBack) {
        if (!NetworkUtil.INSTANCE.isConnected()) {
            if (callBack != null)
                callBack.receive(new ApiResult(ResultCode.NETWORK_TROBLE, "没有网络啊！！！"));
            return;
        }

        final Builder builder = mBuilder;
//        builder.params = builder.params.getWebSocketParams(builder.url);
        listenerWebSocketResult(builder, callBack);
        RxWebSocket.Companion.getInstance().asyncSend(builder.params.getParams());
    }


    /**
     * @param builder
     * @param content
     * @return
     */
    private ApiResult parseOriginData(Builder builder, String content) {

        ApiResult result = new ApiResult();

        result.setJson(content);
        JSONObject object = JSONObject.parseObject(content);
        if (object.containsKey("state") && object.getBoolean("state")) {
            //网络数据，逻辑成功
            result.setCode(ResultCode.RESULT_OK);
            String data = object.getString("data");
            if (!TextUtils.isEmpty(data))
                result.setResult(parseData(data, builder.clazz, builder.bodyType));
        } else {
            result.setCode(ResultCode.RESULT_FAILED);
            result.setErrorCodeMsg(object.getString("errcode"), object.getString("errmsg"));
        }

        return result;
    }


    /**
     * @param callBack
     * @param listener
     */
    public void uploadFile(final ApiCallBack<String> callBack, final FileProgressListener listener) {
        Builder builder = mBuilder;

        File file = new File(builder.fileUrl);
        if (!file.exists())
            throw new IllegalArgumentException("上传的文件不存在--->" + file.getAbsolutePath());
//
//        progressListener = listener;

        ProgressRequestBody filebody = new ProgressRequestBody(RequestBody.create(MediaType.parse("multipart/form-data"), file), listener);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), filebody);

        String fileExt = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (TextUtils.isEmpty(fileExt))
            fileExt = "jpg";

        int fileMode = FileUtil.INSTANCE.getFileUploadType(fileExt);

        String safe = "1";
        String cache = "1";

        String key = Utils.INSTANCE.MD5(fileMode + fileExt + safe + Utils.INSTANCE.getFileMD5(file) + "taiheUp@#");
        StringBuffer secureKey = new StringBuffer();
//        String mapFrom = "0123456789abcdef";
        String mapTo = "f7c8d0e1a9b53426";
        for (int i = 0; i < key.length(); i++) {
            int tst = Integer.parseInt(key.substring(i, i + 1), 16);
            secureKey.append(mapTo.substring(tst, tst + 1));
        }

        String url = "/?upload=1&fileMode=" + fileMode + "&fileExt=" + fileExt + "&safe=" + safe + "&cache=" + cache + "&mode=upload&secureKey=" + secureKey.toString();

        Call<ResponseBody> mCall = retrofit.create(ApiService.class).uploadFile(url, filePart);
        putCall(builder, mCall);
        startRequest(builder, callBack, mCall);
    }


    /**
     * @param callBack
     * @param listener
     */
    public void uploadFileNew(final ApiCallBack<String> callBack, final FileProgressListener listener) {
        Builder builder = mBuilder;

        File file = new File(builder.fileUrl);
        if (!file.exists())
            throw new IllegalArgumentException("上传的文件不存在--->" + file.getAbsolutePath());

        ProgressRequestBody filebody = new ProgressRequestBody(RequestBody.create(file, MediaType.parse("multipart/form-data")), listener);

        String fileExt = FileUtil.INSTANCE.getFileExtension(file.getAbsolutePath());

        int fileMode = FileUtil.INSTANCE.getFileUploadType(fileExt);

        String safe = "1";//二次验证1.开启验证 2.关闭验证
        String cache = "1";//使用文件重复上传验证.1 开启 2.关闭

        //upload : Key 的组装方式, fileMode+fileExt+safe+文件的md5+每个域名都不同的key 这个串md5后用字符mapping表映射一次.
        String key = Utils.INSTANCE.MD5(fileMode + fileExt + safe + Utils.INSTANCE.getFileMD5(file) + "taiheUp@#");
        StringBuffer secureKey = new StringBuffer();
//        String mapFrom = "0123456789abcdef";
        String mapTo = "f7c8d0e1a9b53426";
        for (int i = 0; i < key.length(); i++) {
            int tst = Integer.parseInt(key.substring(i, i + 1), 16);
            secureKey.append(mapTo.substring(tst, tst + 1));
        }

        Map<String, RequestBody> fileUploadParams = new HashMap<>();
        fileUploadParams.put("fileMode", RequestBody.create(String.valueOf(fileMode), null));
        //(mov count是从视频中抽取的图片数量 0表示不抽取 仅当fileMode是2的时候生效.)
        fileUploadParams.put("movImgCount", RequestBody.create("0", null));
        fileUploadParams.put("fileExt", RequestBody.create(fileExt, null));
        try {
            fileUploadParams.put("file\"; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + " ", filebody);
        } catch (Exception e) {
            e.printStackTrace();
            fileUploadParams.put("file\"; filename=\"" + URLEncoder.encode(file.getName()) + " ", filebody);
        }
        fileUploadParams.put("safe", RequestBody.create(safe, null));
        fileUploadParams.put("cache", RequestBody.create(cache, null));
        fileUploadParams.put("secureKey", RequestBody.create(secureKey.toString(), null));
//        fileUploadParams.put("action", RequestBody.create(builder.url.replace(Constant.apiHead, ""), null));
        fileUploadParams.put("mode", RequestBody.create("upload", null));

//        Call<ResponseBody> mCall = retrofit.create(ApiService.class).uploadFileNew(builder.url.replace(Constant.apiHead, ""), fileUploadParams);
        Call<ResponseBody> mCall = retrofit.create(ApiService.class).uploadFileNew(builder.url, fileUploadParams);
        mCall = mCall.clone();

        putCall(builder, mCall);
        startRequest(builder, callBack, mCall);
    }

    /**
     * 文件下载
     *
     * @param callBack
     * @param listener
     */
    public void downloadFile(final ApiCallBack<File> callBack, final FileProgressListener listener) {
        Builder builder = mBuilder;

        if (builder.saveFile == null) throw new IllegalArgumentException("下载保存的文件地址不能为空");

        progressListener = listener;

        Call<ResponseBody> mCall = retrofit.create(ApiService.class).executeDownloadFile(builder.fileUrl);
        putCall(builder, mCall);
        startDownloadFileRequest(builder, callBack, mCall);
    }

    /**
     * @param builder
     * @param callBack
     */
    private void startDownloadFileRequest(final Builder builder, final ApiCallBack callBack, Call<ResponseBody> mCall) {
        if (!NetworkUtil.INSTANCE.isConnected()) {
            if (callBack != null)
                callBack.receive(new ApiResult(ResultCode.NETWORK_TROBLE, "没有网络啊！！！"));
            return;
        }
        mCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    final File file = builder.saveFile;
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileUtil.INSTANCE.getFileFromBytes(response.body().bytes(), file);

                    if (callBack != null)
                        callBack.receive(new ApiResult(ResultCode.RESULT_OK, file, file.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(null, e);
                }
                progressListener = null;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (callBack != null)
                    callBack.receive(new ApiResult(ResultCode.RESULT_FAILED, t.getMessage()));
                if (null != builder.tag) {
                    removeCall(builder.url);
                }
                progressListener = null;
            }
        });
    }

    /**
     * 发送网络请求
     *
     * @param builder
     * @param callBack
     */
    private void startRequest(final Builder builder, final ApiCallBack callBack, Call<ResponseBody> mCall) {
        if (!NetworkUtil.INSTANCE.isConnected()) {
            if (callBack != null)
                callBack.receive(new ApiResult(ResultCode.NETWORK_TROBLE, "没有网络啊！！！"));
            return;
        }
        mCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                // TODO: 2018/5/19 这里可以根据实际情况做相应的调整 比如
                ApiResult result = null;
                if (200 == response.code()) {
                    try {
                        String re = response.body().string();
                        result = new ApiResult(ResultCode.RESULT_OK, parseData(re, builder.clazz, builder.bodyType), re);
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = new ApiResult(ResultCode.RESULT_FAILED, e.getMessage());
                    }
                }
                if (!response.isSuccessful() || 200 != response.code()) {
                    // TODO: 2018/5/19  这里可以根据实际情况，对返回的错误msg，通过接口的msg来拿
                    result = new ApiResult(ResultCode.RESULT_FAILED, response.message());
                }

                if (callBack != null)
                    callBack.receive(result);

                if (null != builder.tag) {
                    removeCall(builder.url);
                }

//                ApiResult result;
//                try {
//                    result = parseOriginData(builder, response.body().string());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    result = new ApiResult(ResultCode.RESULT_FAILED, e.getMessage());
//                }
//
//                if (callBack != null)
//                    callBack.receive(result);
//
//                if (null != builder.tag) {
//                    removeCall(builder.url);
//                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (callBack != null)
                    callBack.receive(new ApiResult(ResultCode.RESULT_FAILED, t.getMessage()));
                if (null != builder.tag) {
                    removeCall(builder.url);
                }
            }

        });
    }

    /**
     * 数据解析方法
     *
     * @param data     要解析的数据
     * @param clazz    解析类
     * @param bodyType 解析数据类型
     */
    @SuppressWarnings("unchecked")
    private Object parseData(String data, Class clazz, @DataType.Type int bodyType) {
        Object object = null;
        switch (bodyType) {
            case DataType.STRING:
                object = data;
                break;
            case DataType.JSON_OBJECT:
                object = DataParseUtil.parseObject(data, clazz);
                break;
            case DataType.JSON_ARRAY:
                object = DataParseUtil.parseToList(data, clazz);
                break;
        }
        return object;
    }

    /**
     * 添加某个请求
     */
    private synchronized void putCall(Builder builder, Call call) {
        if (builder.tag == null)
            return;
        synchronized (CALL_MAP) {
            CALL_MAP.put(builder.tag.toString() + builder.url, call);
        }
    }


    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求;
     * 如果要取消某个tag单独请求，tag需要传入tag+url
     *
     * @param tag 请求标签
     */
    public synchronized void cancel(Object tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }

    }

    /**
     * 移除某个请求
     *
     * @param url 添加的url
     */
    private synchronized void removeCall(String url) {
        if (TextUtils.isEmpty(url)) return;
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (!TextUtils.isEmpty(url) && key.contains(url)) {
                    url = key;
                    break;
                }
            }
            if (!TextUtils.isEmpty(url))
                CALL_MAP.remove(url);
        }
    }

    /**
     * @param builder
     */
    private void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * 网络请求的builder
     */
    public static final class Builder {
        private String builderBaseUrl = "";
        private String url;
        private Object tag;
        private ApiParams params;
        /*返回数据的类型,默认是string类型*/
        @DataType.Type
        private int bodyType = DataType.STRING;
        /*解析类*/
        private Class clazz;

        private String fileUrl;
        private File saveFile;

        public Builder() {
            params = new ApiParams();
            builderBaseUrl = Constant.webServer;
            tag = null;
        }

        /**
         * 请求地址的baseUrl，最后会被赋值给HttpClient的静态变量BASE_URL；
         *
         * @param baseUrl 请求地址的baseUrl
         */
        public Builder baseUrl(String baseUrl) {
            this.builderBaseUrl = baseUrl;
            return this;
        }

        /**
         * @param fileUrl
         * @return
         */
        public Builder fileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        /**
         * @param saveFile
         * @return
         */
        public Builder saveFile(File saveFile) {
            this.saveFile = saveFile;
            return this;
        }

        /**
         * 除baseUrl以外的部分，
         * 例如："mobile/login"
         *
         * @param url path路径
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * 给当前网络请求添加标签，用于取消这个网络请求
         *
         * @param tag 标签
         */
        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 直接传对象
         *
         * @param params
         * @return
         */
        public Builder params(ApiParams params) {
            this.params = params;
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param key   键
         * @param value 值
         */
        public Builder params(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        /**
         * 响应体类型设置,如果要响应体类型为STRING，请不要使用这个方法
         *
         * @param bodyType 响应体类型，分别:STRING，JSON_OBJECT,JSON_ARRAY,XML
         * @param clazz    指定的解析类
         * @param <T>      解析类
         */
        public <T> Builder bodyType(@DataType.Type int bodyType, @NonNull Class<T> clazz) {
            this.bodyType = bodyType;
            this.clazz = clazz;
            return this;
        }

        /**
         * @return
         */
        public ApiHelper build() {
            ApiHelper client = getInstance();
            client.isWebSocketRequest = false;
            client.setBuilder(this);
            client.getRetrofit();
            return client;
        }

        public ApiHelper webSocket() {
            ApiHelper client = getInstance();
            client.setBuilder(this);
            client.isWebSocketRequest = true;
            return client;
        }
    }
}

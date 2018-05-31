package com.soli.libCommon.net;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.soli.libCommon.base.Constant;
import com.soli.libCommon.net.cookie.PersistentCookieJar;
import com.soli.libCommon.net.cookie.cache.SetCookieCache;
import com.soli.libCommon.net.cookie.https.HttpsUtils;
import com.soli.libCommon.net.cookie.persistence.SharedPrefsCookiePersistor;
import com.soli.libCommon.util.NetworkUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Soli
 * @Time 18-5-17 下午4:42
 */
public class ApiHelper {

    /*用户设置的BASE_URL*/
    private static String BASE_URL = Constant.webServer;
    /*本地使用的baseUrl*/
    private String baseUrl = "";
    private static OkHttpClient.Builder okHttpClient;
    private Retrofit retrofit;
    private Call<ResponseBody> mCall;
    private static final Map<String, Call> CALL_MAP = new HashMap<>();
    private Builder mBuilder;
    private static ApiHelper client;

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
        okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.readTimeout(30, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(30, TimeUnit.SECONDS);
        okHttpClient.retryOnConnectionFailure(true);
        okHttpClient.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Constant.getContext())));

        if (Constant.Debug) {
            okHttpClient.addInterceptor((new HttpLoggingInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY));
            okHttpClient.addNetworkInterceptor(new StethoInterceptor());
        }

        //支持https访问
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClient.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
    }

    /**
     * 获取的Retrofit的实例，
     * 引起Retrofit变化的因素只有静态变量BASE_URL的改变。
     */
    private void getRetrofit() {
        if (!BASE_URL.equals(baseUrl) || retrofit == null) {
            baseUrl = BASE_URL;
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient.build())
                    .build();
        }
    }

    /**
     * 发送get请求
     *
     * @param callBack
     */
    public void get(final ApiCallBack callBack) {
        Builder builder = mBuilder;
        builder.url(builder.url + "?" + builder.params.getParams());
        mCall = retrofit.create(ApiService.class).executeGet(builder.url);
        putCall(builder, mCall);
        startRequest(builder, callBack);
    }

    /**
     * 发送post请求
     *
     * @param callBack
     */
    public void post(final ApiCallBack callBack) {
        Builder builder = mBuilder;
        mCall = retrofit.create(ApiService.class).executePost(builder.url, builder.params);
        putCall(builder, mCall);
        startRequest(builder, callBack);
    }

    /**
     * 发送网络请求
     *
     * @param builder
     * @param callBack
     */
    private void startRequest(final Builder builder, final ApiCallBack callBack) {
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
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
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

        public Builder() {
            params = new ApiParams();
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
            if (!TextUtils.isEmpty(builderBaseUrl)) {
                BASE_URL = builderBaseUrl;
            }
            ApiHelper client = getInstance();
            client.getRetrofit();
            client.setBuilder(this);
            return client;
        }
    }
}

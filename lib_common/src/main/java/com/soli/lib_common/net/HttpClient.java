package com.soli.lib_common.net;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.soli.lib_common.base.Constant;
import com.soli.lib_common.net.cookie.PersistentCookieJar;
import com.soli.lib_common.net.cookie.cache.SetCookieCache;
import com.soli.lib_common.net.cookie.https.HttpsUtils;
import com.soli.lib_common.net.cookie.persistence.SharedPrefsCookiePersistor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * @author Soli
 * @Time 18-5-17 下午4:42
 */
public class HttpClient {

    /*用户设置的BASE_URL*/
    private static String BASE_URL = "";
    /*本地使用的baseUrl*/
    private String baseUrl = "";
    private static OkHttpClient.Builder okHttpClient;
    private Retrofit retrofit;
    private Call<ResponseBody> mCall;
    private static final Map<String, Call> CALL_MAP = new HashMap<>();

    private static HttpClient client;

    public static HttpClient getInstance() {
        if (client != null)
            return client;
        synchronized (HttpClient.class) {
            if (client == null)
                client = new HttpClient();
        }

        return client;
    }

    /**
     *
     */
    private HttpClient() {
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
}

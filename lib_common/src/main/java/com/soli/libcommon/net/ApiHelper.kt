package com.soli.libcommon.net

import android.text.TextUtils
import android.webkit.MimeTypeMap
import com.alibaba.fastjson.JSON
import com.soli.libcommon.base.Constant
import com.soli.libcommon.net.cookie.https.HttpsUtils
import com.soli.libcommon.net.download.FileProgressListener
import com.soli.libcommon.net.download.ProgressInterceptor
import com.soli.libcommon.net.upload.ProgressRequestBody
import com.soli.libcommon.net.websocket.RxWebSocket
import com.soli.libcommon.net.websocket.RxWebSocket.Companion.Instance
import com.soli.libcommon.net.websocket.WebSocketData
import com.soli.libcommon.util.*
import com.soli.libcommon.util.Utils.MD5
import com.soli.libcommon.util.Utils.getFileMD5
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.net.Proxy
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @author Soli
 * @Time 18-5-17 下午4:42
 */
class ApiHelper private constructor(private val builder: Builder) {

    companion object {

        /**
         *
         */
        inline fun build(block: Builder.() -> Unit): ApiHelper = Builder().apply(block).build()

        private val OkHttpCallMap = ConcurrentHashMap<String, Call<*>>()
        private val WebSocketCallMap = ConcurrentHashMap<String, WebSocketData>()

        @Volatile
        private var okHttpClient: OkHttpClient? = null

        @JvmStatic
        val client: OkHttpClient
            get() = okHttpClient ?: synchronized(this) {
                okHttpClient ?: newClient.also { okHttpClient = it }
            }

        @JvmStatic
        fun resetApiClient() {
            retrofit = null
            okHttpClient = null
        }

        @Volatile
        private var retrofit: Retrofit? = null

        private val timeout = 30L

        //文件下载进度回调
        private var progressListener: FileProgressListener? = null

        private val newClient: OkHttpClient
            get() = OkHttpClient.Builder().apply {
                connectTimeout(timeout, TimeUnit.SECONDS)
                readTimeout(timeout, TimeUnit.SECONDS)
                writeTimeout(timeout, TimeUnit.SECONDS)
                retryOnConnectionFailure(true)
                if (!SecurityUtil.needCapturePacket)
                    proxy(Proxy.NO_PROXY)
                //        cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Constant.getContext())));
                hostnameVerifier { _, _ -> true }

                addProgress(this)
                netWorkCacheSet(this)

                //添加公共请求头的参数
                addInterceptor(RequestHeaderInterceptor())
                if (Constant.Debug) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    eventListenerFactory(LoggingEventListener.Factory())
                    //for stetho 在网页调试页看网络日志
                    addNetworkInterceptor(StethoInterceptor())
                }

                //支持https访问  Android 5.0以下 TLSV1.1和TLSV1.2是关闭的，要自己打开，Android 5.0以上是打开的
                //这里就针对这两种情况，不同处理
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                //Android 5.0以上
                val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
                sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                //        } else {
//            //Android 5.0 以下
//            SSLContext sslContext = null;
//            try {
//                sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, null, null);
//
//                SSLSocketFactory socketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());
//                sslSocketFactory(socketFactory, new HttpsUtils.UnSafeTrustManager());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
            }.build()


        /**
         * 网络缓存设置
         *
         * @param builder
         */
        private fun netWorkCacheSet(builder: OkHttpClient.Builder) {
            val mFile = File(Constant.context.cacheDir.toString() + "http") //储存目录
            val maxSize = (10 * 1024 * 1024).toLong() // 10 MB 最大缓存数
            builder.cache(Cache(mFile, maxSize))
            builder.interceptors().add(CacheInterceptor())
            builder.networkInterceptors().add(CacheInterceptor())
        }

        /**
         * 添加对网络数据获取的进度添加
         *
         * @param builder
         */
        private fun addProgress(builder: OkHttpClient.Builder) {
            builder.addNetworkInterceptor(ProgressInterceptor { progress: Int?, bytesRead: Long?, updateBytes: Long?, fileSize: Long?, done: Boolean? ->
                if (progressListener != null) {
                    RxJavaUtil.runOnUiThread {
                        if (progressListener != null) progressListener!!.progress(
                            progress!!, bytesRead!!, updateBytes!!, fileSize!!, done!!
                        )
                    }
                }
            })
        }


        /**
         * 添加某个请求
         */
        private fun putCall(builder: Builder, call: Call<*>) {
            builder.tag ?: return
            OkHttpCallMap[builder.tag.toString() + builder.url] = call
        }

        /**
         *
         */
        private fun removeCall(builder: Builder?) {
            builder ?: return
            builder.tag ?: return
            OkHttpCallMap.remove(builder.tag)
        }

        /**
         * 取消某个界面都所有请求，或者是取消某个tag的所有请求;
         * 如果要取消某个tag单独请求，tag需要传入tag+url
         *
         * @param tag 请求标签
         */
        @JvmStatic
        fun cancel(tag: Any?) {
            tag ?: return
            val list: MutableList<String> = ArrayList()
            for (key in OkHttpCallMap.keys) {
                if (key.startsWith(tag.toString())) {
                    OkHttpCallMap[key]?.cancel()
                    list.add(key)
                }
            }

            list.forEach { OkHttpCallMap.remove(it) }
        }

        /**
         * websocket这个请求这里很重要，每次请求的都缓存起来，回来在处理
         *
         * todo 这里用作测试，具体每个websocket请求都有一个标识这个请求的唯一token
         */
        private fun putWebCall(builder: Builder, data: ApiCallBack<Any>?) {
            data ?: return
            val token = builder.params["token"]
            if (!TextUtils.isEmpty(token))
                WebSocketCallMap[token!!] = WebSocketData(builder, data)
        }

        /**
         *
         */
        private fun getWebCall(token: String?): WebSocketData? {
            token ?: return null
            return if (WebSocketCallMap.contains(token)) WebSocketCallMap[token] else null
        }

        /**
         *
         */
        private fun cancleWebCall(token: String?) {
            token ?: return
            if (WebSocketCallMap.contains(token))
                WebSocketCallMap.remove(token)
        }

        /**
         *
         */
        fun dealWebSocketResult(webBackStr: String?): Boolean {

            var isDealData = false

            webBackStr ?: return isDealData

            val json = JSON.parseObject(webBackStr)
            val backToken = json.getString("token")
            if (!TextUtils.isEmpty(backToken)) {
                val socketData = getWebCall(backToken)
                if (socketData != null) {
                    var result: ApiResult<Any>
                    try {
                        val code = json.getString("code")
                        val content = json.getString("content")
                        val builder = socketData.builer
                        val forwardToken = builder.params["token"].toString()
                        var isOkay = false
                        val tokenMsg = "请求的token:$forwardToken--服务器返回的token:$backToken\t"
                        var msg = tokenMsg + "websocket返回的数据有问题,返回的数据:" + webBackStr
                        if (forwardToken != backToken) {
                            msg = tokenMsg + "请求的token和服务器返回的token不一致"
                        } else if (code == "0" &&
                            !TextUtils.isEmpty(backToken)
                            && !TextUtils.isEmpty(content)
                        ) {
                            isOkay = true
                            msg = tokenMsg
                        }
                        result = if (isOkay) {
                            DataParseUtil.parseOriginData(builder, content) as ApiResult<Any>
                        } else {
                            MLog.e(RxWebSocket.logTag, msg)
                            ApiResult(ResultCode.RESULT_FAILED, msg)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        result = ApiResult(ResultCode.RESULT_FAILED, "${e.message}")
                    }
                    if (socketData.callback != null) {
                        socketData.callback.invoke(result)
                        cancleWebCall(backToken)
                        isDealData = true
                    }
                }
            }

            return isDealData
        }
    }

    /**
     * 获取的Retrofit的实例，
     * 引起Retrofit变化的因素只有静态变量BASE_URL的改变。
     */
    private fun getRetrofit() {

        client ?: return

        //默认的
        val defaultUrl = Constant.webServer
        //动态设置的
        var setUrl = builder.baseUrl
        val isNeedGet = when {
            retrofit == null ||
                    !TextUtils.isEmpty(setUrl) && defaultUrl != setUrl ||
                    retrofit!!.baseUrl().toString() != defaultUrl -> true
            else -> false
        }

        if (isNeedGet) {
            if (TextUtils.isEmpty(setUrl))
                setUrl = defaultUrl
            retrofit = Retrofit.Builder()
                .baseUrl(setUrl)
                .client(client)
                .build()
        }
    }

    /**
     *
     */
    private fun <T> dealNetWorkInfo(callBack: ApiCallBack<T>?): Boolean {
        return when {
            !NetworkUtil.isConnected() -> {
                callBack?.invoke(ApiResult(ResultCode.NETWORK_TROBLE, "----没有网络啦---"))
                true
            }
            SecurityUtil.dealNetSecurityCheck() -> {
                callBack?.invoke(ApiResult(ResultCode.NETWORK_TROBLE, "网络异常"))
                true
            }
            else -> false
        }
    }

    /**
     * 发送get请求
     *
     * @param callBack
     */
    fun <T> get(callBack: ApiCallBack<T>) {
        require(retrofit != null) { "Retrofit不能为空" }
        val mCall = retrofit!!.create(ApiService::class.java).executeGet(builder.getUrl)
        startRequest(callBack, mCall)
    }

    /**
     * 发送post请求
     *
     * @param callBack
     */
    fun <T> post(callBack: ApiCallBack<T>?) {
        val mCall =
            retrofit!!.create(ApiService::class.java).executePost(builder.url, builder.params)
        startRequest(callBack, mCall)
    }

    /**
     * 默认发送post请求
     *
     * @param callBack
     */
    fun <T> request(callBack: ApiCallBack<T>?) {
        if (!builder.isWebSocketRequest) post(callBack) else webSocketRequest(callBack)
    }

    /**
     * 监听websocket回来的数据并处理
     *
     * @param builder
     * @param callBack
     */
    private fun <T> listenerWebSocketResult(callBack: ApiCallBack<T>?) {
        val disposable = Instance.getWebSocketInfoObservable()
            .timeout(10, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .filter { (_, string) -> string != null }
            .take(1)
            .subscribe({ (_, data) ->
                callBack?.invoke(
                    try {
                        ApiResult(
                            code = ResultCode.RESULT_OK,
                            fullData = data ?: "",
                            result = DataParseUtil.parseData(
                                builder.isJavaModel,
                                data ?: "",
                                builder.clazz,
                                builder.bodyType
                            )
                        )
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
                    } catch (e: Exception) {
                        ApiResult(ResultCode.RESULT_FAILED, e.message ?: "")
                    }
                )
            }) { throwable ->
                callBack?.invoke(ApiResult(ResultCode.RESULT_FAILED, throwable.message!!))
            }
    }

    /**
     * 每个请求的超时时间为10s,就是如果10s,对应的token数据都还没回来，那么就timeoutException
     *
     * @param callBack
     */
    private fun <T> webSocketRequest(callBack: ApiCallBack<T>?) {
        if (dealNetWorkInfo(callBack)) return

        //        builder.params = builder.params.getWebSocketParams(builder.url);
        putWebCall(builder, callBack as ApiCallBack<Any>)

        //todo  listenerWebSocketResult下面这个函数，正常情况下，不需要，回来的数据会走【RxWebSocket.keepOnline】来处理
        listenerWebSocketResult(callBack)

        Instance.asyncSend(builder.params.params)
    }

    /**
     * @param callBack
     * @param listener
     */
    fun uploadFile(callBack: ApiCallBack<String?>?, listener: FileProgressListener?) {

        if (dealNetWorkInfo(callBack)) return

        require(File(builder.fileUrl).exists()) { "下载的文件地址有问题" }

        val file = File(builder.fileUrl)
        require(file.exists()) { "上传的文件不存在--->" + file.absolutePath }
        //
//        progressListener = listener;
        val filebody = ProgressRequestBody(
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull()),
            listener
        )
        val filePart = MultipartBody.Part.createFormData("file", file.name, filebody)
        var fileExt = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
        if (TextUtils.isEmpty(fileExt)) fileExt = "jpg"
        val fileMode = FileUtil.getFileUploadType(fileExt)
        val safe = "1"
        val cache = "1"
        val key = MD5(fileMode.toString() + fileExt + safe + getFileMD5(file) + "taiheUp@#")
        val secureKey = StringBuffer()
        //        String mapFrom = "0123456789abcdef";
        val mapTo = "f7c8d0e1a9b53426"
        for (i in key.indices) {
            val tst = key.substring(i, i + 1).toInt(16)
            secureKey.append(mapTo.substring(tst, tst + 1))
        }
        val url =
            "/?upload=1&fileMode=$fileMode&fileExt=$fileExt&safe=$safe&cache=$cache&mode=upload&secureKey=$secureKey"
        val mCall = retrofit!!.create(ApiService::class.java).uploadFile(url, filePart)

        startRequest(callBack, mCall)
    }

    /**
     * @param callBack
     * @param listener
     */
    fun uploadFileNew(callBack: ApiCallBack<String>?, listener: FileProgressListener?) {

        if (dealNetWorkInfo(callBack)) return

        val file = File(builder.fileUrl)
        require(file.exists()) { "上传的文件不存在--->" + file.absolutePath }
        val filebody = ProgressRequestBody(
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull()),
            listener
        )
        val fileExt = FileUtil.getFileExtension(file.absolutePath)
        val fileMode = FileUtil.getFileUploadType(fileExt)
        val safe = "1" //二次验证1.开启验证 2.关闭验证
        val cache = "1" //使用文件重复上传验证.1 开启 2.关闭

        //upload : Key 的组装方式, fileMode+fileExt+safe+文件的md5+每个域名都不同的key 这个串md5后用字符mapping表映射一次.
        val key = MD5(fileMode.toString() + fileExt + safe + getFileMD5(file) + "taiheUp@#")
        val secureKey = StringBuffer()
        //        String mapFrom = "0123456789abcdef";
        val mapTo = "f7c8d0e1a9b53426"
        for (i in key.indices) {
            val tst = key.substring(i, i + 1).toInt(16)
            secureKey.append(mapTo.substring(tst, tst + 1))
        }
        val fileUploadParams: MutableMap<String, RequestBody> = HashMap()
        fileUploadParams["fileMode"] = fileMode.toString().toRequestBody()
        //(mov count是从视频中抽取的图片数量 0表示不抽取 仅当fileMode是2的时候生效.)
        fileUploadParams["movImgCount"] = "0".toRequestBody()
        fileUploadParams["fileExt"] = fileExt.toRequestBody()
        try {
            fileUploadParams["file\"; filename=\"" + URLEncoder.encode(file.name, "UTF-8") + " "] =
                filebody
        } catch (e: Exception) {
            e.printStackTrace()
            fileUploadParams["file\"; filename=\"" + URLEncoder.encode(file.name) + " "] = filebody
        }
        fileUploadParams["safe"] = safe.toRequestBody()
        fileUploadParams["cache"] = cache.toRequestBody()
        fileUploadParams["secureKey"] = secureKey.toString().toRequestBody()
        //        fileUploadParams.put("action", RequestBody.create(builder.url.replace(Constant.apiHead, ""), null));
        fileUploadParams["mode"] = "upload".toRequestBody()

//        Call<ResponseBody> mCall = retrofit.create(ApiService.class).uploadFileNew(builder.url.replace(Constant.apiHead, ""), fileUploadParams);
        var mCall =
            retrofit!!.create(ApiService::class.java).uploadFileNew(builder.url, fileUploadParams)
        mCall = mCall.clone()
        startRequest(callBack, mCall)
    }

    /**
     * 文件下载
     *
     * @param callBack
     * @param listener
     */
    fun downloadFile(callBack: ApiCallBack<File>?, listener: FileProgressListener?) {
        requireNotNull(builder.saveFile) { "下载保存的文件地址不能为空" }
        progressListener = listener
        val mCall = retrofit!!.create(ApiService::class.java).executeDownloadFile(
            builder.fileUrl
        )
        putCall(builder, mCall)
        startDownloadFileRequest(callBack, mCall)
    }

    /**
     * @param builder
     * @param callBack
     */
    private fun startDownloadFileRequest(
        callBack: ApiCallBack<File>?,
        mCall: Call<ResponseBody>
    ) {
        if (dealNetWorkInfo(callBack)) return

        mCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    val file = builder.saveFile
                    if (!file!!.exists()) {
                        file.createNewFile()
                    }
                    GlobalScope.launch {
                        FileUtil.getFileFromInputStream(response.body()!!.byteStream(), file)
                        withContext(Dispatchers.Main) {
                            callBack?.invoke(
                                ApiResult(
                                    code = ResultCode.RESULT_OK,
                                    result = file,
                                    fullData = file.absolutePath
                                )
                            )
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    onFailure(null, e)
                }

                removeCall(builder)
                progressListener = null
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable) {
                removeCall(builder)
                progressListener = null
                t.printStackTrace()
                callBack?.invoke(ApiResult(ResultCode.RESULT_FAILED, t.message!!))
            }
        })
    }

    /**
     * 发送网络请求
     *
     * @param builder
     * @param callBack
     */
    private fun <T> startRequest(callBack: ApiCallBack<T>?, mCall: Call<ResponseBody>) {

        if (dealNetWorkInfo(callBack)) return

        putCall(builder, mCall)

        mCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // TODO: 2018/5/19 这里可以根据实际情况做相应的调整 比如
                var result: ApiResult<T>? = null
                if (200 == response.code()) {
                    result = try {
                        val data = response.body()!!.string()
//                        DataParseUtil.parseOriginData(builder,data) as ApiResult<T>
                        ApiResult(
                            code = ResultCode.RESULT_OK,
                            fullData = data,
                            result = DataParseUtil.parseData(
                                builder.isJavaModel,
                                data,
                                builder.clazz,
                                builder.bodyType
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ApiResult(ResultCode.RESULT_FAILED, e.message ?: "")
                    }
                }
                if (!response.isSuccessful || 200 != response.code()) {
                    // TODO: 2018/5/19  这里可以根据实际情况，对返回的错误msg，通过接口的msg来拿
                    result = ApiResult(ResultCode.RESULT_FAILED, response.message() ?: "")
                }
                if (result != null)
                    callBack?.invoke(result)

                removeCall(builder)

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

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                removeCall(builder)
                callBack?.invoke(ApiResult(ResultCode.RESULT_FAILED, t.message!!))
            }
        })
    }

    /**
     * 网络请求的builder
     */
    class Builder {
        var baseUrl = Constant.webServer

        //        var baseUrl = ""
        var url = ""
//            set(value) {
//                field = if (!value.contains("v1")) {
//                    (if (value.startsWith("/")) "/v1" else "/v1/") + value
//                } else
//                    value
//            }

        var tag: Any? = null

        var fileUrl = ""
            set(value) {
                field = value
                MLog.d("多媒体加载downUp", value)
            }

        var saveFile: File? = null
            set(value) {
                field = value
                MLog.d("多媒体加载save", value?.absolutePath)
            }

        //true表示webSocket请求
        var isWebSocketRequest = false

        var params: ApiParams = ApiParams()

        /*返回数据的类型,默认是string类型*/
        @DataType.Type
        var bodyType = DataType.STRING

        /**
         *
         */
        var clazz: Class<*>? = null

        //        是否解析java model，如果true 就用fastjson解析
        var isJavaModel = false


//        val urlForRequest: String
//            get() = if (Constant.requestServer.endsWith("/")) {
//                Constant.requestServer + if (url.startsWith("/")) url.substring(1) else url
//            } else
//                Constant.requestServer + if (url.startsWith("/")) url else "/$url"

        val getUrl: String
            get() = "$url?${params.params}"

        fun build() =
            ApiHelper(this).apply {
                getRetrofit()
            }
    }

}
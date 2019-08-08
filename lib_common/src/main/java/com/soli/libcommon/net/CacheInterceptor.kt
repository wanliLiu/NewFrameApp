package com.soli.libcommon.net

import com.soli.libcommon.util.NetworkUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 网络缓存设置
 * @author Soli
 * @Time 18-6-7 下午3:57
 */
class CacheInterceptor : Interceptor {

    private val NET_MAX = 30 //30秒  有网超时时间
    private val NO_NET_MAX = 60 * 60 * 24 * 7 //7天 无网超时时
    override fun intercept(chain: Interceptor.Chain?): Response {

        var request = chain!!.request()
        request = if (!NetworkUtil.isConnected()) {
            request.newBuilder()
                    //Pragma:no-cache。在HTTP/1.1协议中，它的含义和Cache-Control:no-cache相同。为了确保缓存生效
                    .removeHeader("Pragma")
                    .header("Cache-Control", "private, only-if-cached, max-stale=" + NO_NET_MAX)
                    .build()
        } else {
            request.newBuilder()
                    //Pragma:no-cache。在HTTP/1.1协议中，它的含义和Cache-Control:no-cache相同。为了确保缓存生效
                    .removeHeader("Pragma")
                    .header("Cache-Control", "private, max-age=" + NET_MAX)//添加缓存请求头
                    .build()
        }

        return chain.proceed(request)
    }
}
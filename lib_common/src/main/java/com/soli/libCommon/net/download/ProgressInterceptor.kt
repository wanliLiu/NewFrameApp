package com.soli.libCommon.net.download

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Soli
 * @Time 18-6-7 上午11:07
 */
class ProgressInterceptor(
        val progressCallBack: (progress: Int, bytesRead: Long, fileSize: Long, done: Boolean) -> Unit)
    : Interceptor {


    override fun intercept(chain: Interceptor.Chain?): Response {
        val originalResponse = chain!!.proceed(chain.request())

        return originalResponse
                .newBuilder()
                .body(ProgressResponseBody.create(originalResponse.body()!!, progressCallBack))
                .build()
    }
}
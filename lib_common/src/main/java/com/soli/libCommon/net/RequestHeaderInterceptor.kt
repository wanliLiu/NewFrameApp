package com.soli.libCommon.net

import okhttp3.Interceptor
import okhttp3.Response

/**
 *
 * @author Soli
 * @Time 2018/11/22 10:41
 */
class RequestHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val newRequest = chain.request().newBuilder()
//            .header("client_v", MyTools.getVersionStr(Constant.getContext()))
            .header("platform", "android")
            .header("platform_v", android.os.Build.VERSION.RELEASE)
            .header("model", android.os.Build.MODEL)
//            .header("device_id", Utils.getPhoneUUID(Constant.getContext()))
//            .header("token", AuthInfo.getToken())
            //有些图片出现的403 Forbidden
//            .header("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
            .build()

        //FIXME  用户登录还需要添加  uid 用户编号(登陆状态后的所有接口包含此属性)

        return chain.proceed(newRequest)
    }

}
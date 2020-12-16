package com.soli.libcommon.net

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author Soli
 * @Time 18-5-17 下午4:39
 */
interface ApiService {

    @GET
    fun executeGet(@Url url: String): Call<ResponseBody>

    /**
     * POST方式将以表单的方式传递键值对作为请求体发送到服务器
     * 其中@FormUrlEncoded 以表单的方式传递键值对
     * 其中 @Path：所有在网址中的参数（URL的问号前面）
     * 另外@FieldMap 用于POST请求，提交多个表单数据，@Field：用于POST请求，提交单个数据
     * 使用@url是为了防止URL被转义为https://10.33.31.200:8890/msp%2Fmobile%2Flogin%3
     */
    @FormUrlEncoded
    @POST
    fun executePost(@Url url: String, @FieldMap map: Map<String, String>): Call<ResponseBody>

    /**
     * 流式下载,不加这个注解的话,会整个文件字节数组全部加载进内存,可能导致oom
     */
    @Streaming
    @GET
    fun executeDownloadFile(@Url fileUrl: String): Call<ResponseBody>

    @Multipart
    @POST
    fun uploadFile(@Url url: String, @Part file: MultipartBody.Part): Call<ResponseBody>

    @Multipart
    @POST
    fun uploadFileNew(
        @Url url: String,
        @PartMap params: Map<String, RequestBody>
    ): Call<ResponseBody>
}
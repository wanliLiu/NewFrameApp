package com.soli.libCommon.net;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author Soli
 * @Time 18-5-17 下午4:39
 */
public interface ApiService {

    @GET
    Call<ResponseBody> executeGet(@Url String url);

    /**
     * POST方式将以表单的方式传递键值对作为请求体发送到服务器
     * 其中@FormUrlEncoded 以表单的方式传递键值对
     * 其中 @Path：所有在网址中的参数（URL的问号前面）
     * 另外@FieldMap 用于POST请求，提交多个表单数据，@Field：用于POST请求，提交单个数据
     * 使用@url是为了防止URL被转义为https://10.33.31.200:8890/msp%2Fmobile%2Flogin%3
     */
    @FormUrlEncoded
    @POST
    Call<ResponseBody> executePost(@Url String url, @FieldMap Map<String, String> map);


    /**
     * 流式下载,不加这个注解的话,会整个文件字节数组全部加载进内存,可能导致oom
     */
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String fileUrl, @HeaderMap Map<String, String> headers);

}

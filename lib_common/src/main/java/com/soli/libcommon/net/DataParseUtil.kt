package com.soli.libcommon.net

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.soli.libcommon.util.ParameterizedTypeImpl

/*
 *  相关接口解析处理
 * @author soli
 * @Time 2018/5/19 23:15
 */
object DataParseUtil {

    /**
     * 统一解析网络数据返回格式
     * @param builder
     * @param content
     * @return
     */
    @JvmStatic
    fun parseOriginData(builder: ApiHelper.Builder, content: String?): ApiResult<*> {
        checkNotNull(content)

        val result: ApiResult<*> = ApiResult<Any?>()
        result.fullData = content
        val json = JSONObject.parseObject(content)
        if (json.containsKey("state") && json.getBoolean("state")) {
            if (json.containsKey("data")) {
                val data = json.getString("data")
                if (!TextUtils.isEmpty(data)) {
                    //网络数据，逻辑成功
                    result.code = ResultCode.RESULT_OK
                    result.result =
                        parseData(builder.isJavaModel, data, builder.clazz, builder.bodyType)
                }
            }
        } else {
            result.code = ResultCode.RESULT_FAILED
            result.errorCode = json.getString("errcode")
            result.errormsg = json.getString("errmsg")
        }

        return result
    }


    /**
     *
     */
    fun <T> parseData(
        isJavaModel: Boolean,
        data: String, clazz: Class<*>?, @DataType.Type bodyType: Int
    ): T? {
        return if (isJavaModel) {
            when (bodyType) {
                DataType.STRING -> data as T
                DataType.JSON_OBJECT -> JSON.parseObject(data, clazz) as T
                DataType.JSON_ARRAY -> JSON.parseArray(data, clazz) as T
                else -> null
            }
        } else {
            when (bodyType) {
                DataType.STRING -> data as T
                DataType.JSON_OBJECT -> Gson().fromJson(data, clazz) as T
                DataType.JSON_ARRAY -> Gson().fromJson<List<T>>(
                    data,
                    ParameterizedTypeImpl(clazz!!)
                ) as T
                else -> null
            }
        }
    }
}
package com.soli.libcommon.net

import com.soli.libcommon.util.MLog


/**
 * Api请求参数
 */
//typealias ApiParams = HashMap<String, String>

inline fun apiParamsOf(vararg pairs: Pair<String, Any>) = ApiParams().apply {
    for ((key, value) in pairs) {
        put(key, value.toString())
    }
}

inline fun ApiParams.apiParamsOf(vararg pairs: Pair<String, Any>) {
    for ((key, value) in pairs) {
        put(key, value.toString())
    }
}

/**
 * api 请求回调
 */
typealias ApiCallBack<T> = (result: ApiResult<T>) -> Unit

/*
 * 网络请求的结果
 * @author soli
 * @Time 2018/5/20 16:40
 */
class ApiResult<T>(
    var code: ResultCode = ResultCode.RESULT_FAILED,//
    var errorCode: String = "",
    var errormsg: String = "",
    var fullData: String = "",//返回json数据
    var result: T? = null//返回T数据
) {

    constructor(mCode: ResultCode, mErrormsg: String) : this(code = mCode, errormsg = mErrormsg)

    constructor(mResult: T, mfullData: String) : this(code = ResultCode.RESULT_OK,result = mResult, fullData = mfullData)

    /**
     * @return
     */
    val isSuccess: Boolean
        get() {
            val isOk = code === ResultCode.RESULT_OK
            if (!isOk) {
                // TODO: 2018/5/19 网络发生错误的时候，这里可以统一提示啥处理
                MLog.e("OKHTTP", "$errorCode--->$errormsg")
            }
            return isOk
        }

}
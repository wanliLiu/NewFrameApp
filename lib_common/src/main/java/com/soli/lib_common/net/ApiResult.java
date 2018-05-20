package com.soli.lib_common.net;

/*
 * 网络请求的结果
 * @author soli
 * @Time 2018/5/20 16:40
 */
public class ApiResult<T> {

    //网络请求的全部数据
    private String json;
    //转换后的数据
    private T result;

    private ResultCode code;

    //错误信息
    private String errorCode, errormsg;

    /**
     * @param mCode
     * @param mResult
     * @param mJson
     */
    public ApiResult(ResultCode mCode, T mResult, String mJson) {
        this.code = mCode;
        this.result = mResult;
        this.json = mJson;
    }

    /**
     * @param mCode
     * @param merrorCode
     * @param errormsg
     */
    public ApiResult(ResultCode mCode, String merrorCode, String errormsg) {
        this.code = mCode;
        this.errorCode = merrorCode;
        this.errormsg = errormsg;
    }

    /**
     * @param mCode
     * @param errormsg
     */
    public ApiResult(ResultCode mCode, String errormsg) {
        this.code = mCode;
        this.errormsg = errormsg;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        // TODO: 2018/5/19 网络发生错误的时候，这里可以统一提示啥处理
        return code == ResultCode.RESULT_OK;
    }

    /**
     * 返回json数据
     *
     * @return
     */
    public String getFullData() {
        return json;
    }

    /**
     * 返回T数据
     *
     * @return
     */
    public T getResult() {
        return result;
    }

    /**
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @return
     */
    public String getErrormsg() {
        return errormsg;
    }

    /**
     * @return
     */
    public ResultCode getCode() {
        return code;
    }
}

package com.soli.libCommon.net;

/**
 * @author Soli
 * @Time 18-5-17 下午4:08
 */
public interface ApiCallBack<T> {
    /**
     * 网络返回结果
     *
     * @param result
     */
    void receive(ApiResult<T> result);
}

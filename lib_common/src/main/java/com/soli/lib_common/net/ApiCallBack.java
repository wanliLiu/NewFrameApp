package com.soli.lib_common.net;

/**
 * @author Soli
 * @Time 18-5-17 下午4:08
 */
public abstract class ApiCallBack<T> {

    /**
     * 请求成功的情况
     *
     * @param result 需要解析的解析类
     * @param json   接口请求返回的全部参数
     */
    public abstract void onSuccess(T result, String json);

    /**
     * @param type
     * @param message
     */
    public void failure(ErrorType type, String message) {
        // TODO: 2018/5/19 网络发生错误的时候，这里可以统一提示啥处理
        onFailure(type, message);
    }

    /**
     * 请求失败的情况
     *
     * @param type    错误类型，目前就数据错误和网络错误
     * @param message 失败信息
     */
    public abstract void onFailure(ErrorType type, String message);
}

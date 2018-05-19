package com.soli.lib_common.net;

/**
 * 请求结果码
 *
 * @author Soli
 * @Time 18-5-17 下午3:52
 */
public enum ErrorType {

    /**
     * 失败 false
     */
    RESULT_FAILED(0x1),

    /**
     * 网络连接有问题
     **/
    NETWORK_TROBLE(0x2);

    private int mIntValue;

    ErrorType(int intValue) {
        mIntValue = intValue;
    }

    public int getIntValue() {
        return mIntValue;
    }
}

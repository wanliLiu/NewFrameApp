package com.soli.lib_common.net;

/**
 * 请求结果码
 *
 * @author Soli
 * @Time 18-5-17 下午3:52
 */
public enum ResultCode {

    /**
     * 请求成功
     */
    RESULT_OK(0x00),

    /**
     * 失败 false
     */
    RESULT_FAILED(0x1),

    /**
     * 网络连接有问题
     **/
    NETWORK_TROBLE(0x2);

    private int mIntValue;

    ResultCode(int intValue) {
        mIntValue = intValue;
    }

    public int getIntValue() {
        return mIntValue;
    }
}

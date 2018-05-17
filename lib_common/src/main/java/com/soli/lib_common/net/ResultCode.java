package com.soli.lib_common.net;

/**
 * 请求结果码
 *
 * @author Soli
 * @Time 18-5-17 下午3:52
 */
public enum ResultCode {

    /**
     * 成功 true
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


    static ResultCode mapIntToValue(final int stateInt) {
        for (ResultCode value : ResultCode.values()) {
            if (stateInt == value.getIntValue()) {
                return value;
            }
        }
        // If not, return default
        return RESULT_FAILED;
    }

    private int mIntValue;

    ResultCode(int intValue) {
        mIntValue = intValue;
    }

    public int getIntValue() {
        return mIntValue;
    }
}

package com.soli.libcommon.net

/**
 * 请求结果码
 *
 * @author Soli
 * @Time 18-5-17 下午3:52
 */
enum class ResultCode(intValue: Int) {
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
     */
    NETWORK_TROBLE(0x2);

}
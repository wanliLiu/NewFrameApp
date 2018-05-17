package com.soli.lib_common.net;

import com.soli.lib_common.util.ToastUtils;

/**
 * @author Soli
 * @Time 18-5-17 下午3:53
 */
public class Result {

    private final String key_state = "state";
    private final String key_errMsg = "errMsg";
    private final String key_result = "result";

    private String code, msg, result;

    /**
     * 成功失败
     */
    private ResultCode success;

    /**
     * 数据成功
     *
     * @param success
     * @param result
     */
    public Result(ResultCode success, String result) {
        this.success = success;
        this.result = result;
    }

    public Result(ResultCode success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        if (success != ResultCode.RESULT_OK) {
            showToast();
        }

        return success == ResultCode.RESULT_OK;
    }

    /**
     *
     */
    private void showToast() {
        ToastUtils.showShortToast(msg);
    }

}

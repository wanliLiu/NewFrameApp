package com.soli.libcommon.util

import com.orhanobut.logger.Logger
import com.soli.libcommon.base.Constant

/**
 * 全局打印日志工具类
 */
object MLog {
    /**
     * 是否开启全局打印日志
     */
    const val DEFAULT_LOG_TAG = "FrameTag"

    private val Debug: Boolean
        get() = Constant.Debug

    /**
     * @param msg
     * @return
     */
    private fun getMessage(msg: String): String {
//        StackTraceElement[] sElements = new Throwable().getStackTrace();
//        String className = sElements[1].getFileName();
//        String methodName = sElements[1].getMethodName();
//        int lineNumber = sElements[1].getLineNumber();
//
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(className + "---");
//        buffer.append(methodName + "---");
//        buffer.append(lineNumber + "---");
//        buffer.append(getMessage(msg));
//
//        return buffer.toString();

        return msg
    }


    fun i(msg: String?, TAG: String = DEFAULT_LOG_TAG) {
        msg ?: return

        if (Debug) {
//            Log.i(TAG, getMessage(msg))
//            Logger.i("[$TAG] ${getMessage(msg)}")
            Logger.t(TAG).i(msg)
        }
    }

    fun e(TAG: String, msg: String?) {
        msg ?: return
        if (Debug) {
//            Log.e(TAG, getMessage(msg))
//            Logger.e("[$TAG] ${getMessage(msg)}")
            Logger.t(TAG).e(msg)
        }
    }

    @JvmStatic
    fun d(TAG: String, msg: String?) {
        msg ?: return
        if (Debug) {
//            Log.d(TAG, getMessage(msg))
//            Logger.d("[$TAG] ${getMessage(msg)}")
            Logger.t(TAG).d(msg)
        }
    }

    fun v(msg: String?, TAG: String = DEFAULT_LOG_TAG) {
        msg ?: return
        if (Debug) {
//            Log.v(TAG, getMessage(msg))
//            Logger.v("[$TAG] ${getMessage(msg)}")
            Logger.t(TAG).v(msg)
        }
    }

    fun w(msg: String?, TAG: String = DEFAULT_LOG_TAG) {
        msg ?: return
        if (Debug) {
//            Log.w(TAG, getMessage(msg))
//            Logger.w("[$TAG] ${getMessage(msg)}")
            Logger.t(TAG).w(msg)
        }
    }
}
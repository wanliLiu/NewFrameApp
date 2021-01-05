package com.soli.libcommon.util;

import android.util.Log;
import com.soli.libcommon.base.Constant;


/**
 * 全局打印日志工具类
 */
public class MLog {

    /**
     * 是否开启全局打印日志
     */

    private static final String DEFAULT_TAG = "FrameTag";

    /**
     * @param msg
     * @return
     */
    private static String getMessage(String msg) {
//        StackTraceElement[] sElements = new Throwable().getStackTrace();
//        String className = sElements[1].getFileName();
//        String methodName = sElements[1].getMethodName();
//        int lineNumber = sElements[1].getLineNumber();
//
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(className + "---");
//        buffer.append(methodName + "---");
//        buffer.append(lineNumber + "---");
//        buffer.append(msg);
//
//        return buffer.toString();

        return msg;
    }

    public static void i(String TAG, String msg) {
        if (Constant.getDebug()) {
            Log.i(TAG, getMessage(msg));
        }
    }

    public static void e(String TAG, String msg) {
        if (Constant.getDebug()) {
            Log.e(TAG, getMessage(msg));
        }
    }

    public static void d(String TAG, String msg) {
        if (Constant.getDebug()) {
            Log.d(TAG, getMessage(msg));
        }
    }

    public static void v(String TAG, String msg) {
        if (Constant.getDebug()) {
            Log.v(TAG, getMessage(msg));
        }
    }

    public static void w(String TAG, String msg) {
        if (Constant.getDebug()) {
            Log.w(TAG, getMessage(msg));
        }
    }

    public static void i(String msg) {
        if (Constant.getDebug()) {
            Log.i(DEFAULT_TAG, getMessage(msg));
        }
    }

    public static void e(String msg) {
        if (Constant.getDebug()) {
            Log.e(DEFAULT_TAG, getMessage(msg));
        }
    }

    public static void d(String msg) {
        if (Constant.getDebug()) {
            Log.d(DEFAULT_TAG, getMessage(msg));
        }
    }

    public static void v(String msg) {
        if (Constant.getDebug()) {
            Log.v(DEFAULT_TAG, getMessage(msg));
        }
    }

    public static void w(String msg) {
        if (Constant.getDebug()) {
            Log.w(DEFAULT_TAG, getMessage(msg));
        }
    }

}

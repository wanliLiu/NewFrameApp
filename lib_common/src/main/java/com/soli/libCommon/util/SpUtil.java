package com.soli.libCommon.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.soli.libCommon.base.Constant;

/**
 * @author Soli
 * @Time 18-5-17 下午4:14
 */
public class SpUtil {

    private static final String defaultName = "frame_save";

    /**
     * @param fileName
     * @return
     */
    public static SharedPreferences getSP(String fileName) {
        return Constant.getContext().getSharedPreferences(fileName, Context.MODE_MULTI_PROCESS);
    }

    /**
     * @param key
     * @param value
     */
    public static void putValue(String key, Object value) {
        putValue(defaultName, key, value);
    }

    /**
     * @param fileName
     * @param key
     * @param value
     */
    public static void putValue(String fileName, String key, Object value) {
        SharedPreferences.Editor sp = getSP(fileName).edit();
        sp.putString(key, String.valueOf(value));
        sp.apply();
    }

    /**
     * @param key
     * @return
     */
    public static String getValue(String key) {
        return getValue(defaultName, key);
    }

    /**
     * @param fileName
     * @param key
     * @return
     */
    public static String getValue(String fileName, String key) {
        return getSP(fileName).getString(key, "");
    }

    /**
     * @param fileName
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getValue(String fileName, String key, String defaultValue) {
        return getSP(fileName).getString(key, defaultValue);
    }
}

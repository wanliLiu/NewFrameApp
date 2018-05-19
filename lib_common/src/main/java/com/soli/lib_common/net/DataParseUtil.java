package com.soli.lib_common.net;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

/*
 * @author soli
 * @Time 2018/5/19 23:15
 */
public class DataParseUtil {

    private DataParseUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 解析json对象
     *
     * @param json 要解析的json
     * @param clazz  解析类
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }

    /**
     * 解析json数组为ArrayList
     *
     * @param json  要解析的json
     * @param clazz 解析类
     * @return ArrayList
     */
    public static <T> List<T> parseToList(String json, Class<T> clazz) {
        return JSONObject.parseArray(json, clazz);
    }
}

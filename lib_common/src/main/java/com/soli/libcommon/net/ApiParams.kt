package com.soli.libcommon.net

import java.util.*

/**
 * @author Soli
 * @Time 18-5-17 下午4:44
 */
class ApiParams : HashMap<String, String>() {// 删除最后一个&

    init {
        // TODO: 18-5-17 公共参数的添加 看和后台的约定，因为有些是这样干的，有些是直接加载header里面的
        put("terminal", "android")
        put("sysVersion", "6.0.1")
        put("appVersion", "3.0.0")
        put("sign", "601e19c8fcb70a99982a2ca0952add2d")
        put("userId", "381358")
        put("childId", "0")
        put("userType", "3")
    }

    /**
     * 获取get的参数组合
     *
     * @return
     */
    val params: String
        get() {
            val sp = StringBuffer("")
            val iter: Iterator<Map.Entry<String, String>> = entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                val key = entry.key
                val value = entry.value
                sp.append(key).append("=").append(value).append("&")
            }
            // 删除最后一个&
            if (sp.length > 1) sp.deleteCharAt(sp.length - 1)
            return sp.toString()
        }


}
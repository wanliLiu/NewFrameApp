package com.soli.lib_common.net;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Soli
 * @Time 18-5-17 下午4:44
 */
public class ApiParams extends HashMap<String, String> {

    public ApiParams() {
        // TODO: 18-5-17 公共参数的添加
    }

    /**
     * 获取get的参数组合
     *
     * @return
     */
    public String getParams() {
        StringBuffer sp = new StringBuffer("");
        Iterator<Entry<String, String>> iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();

            sp.append(key).append("=").append(val).append("&");
        }
        // 删除最后一个&
        if (sp.length() > 1)
            sp.deleteCharAt(sp.length() - 1);

        return sp.toString();
    }
}

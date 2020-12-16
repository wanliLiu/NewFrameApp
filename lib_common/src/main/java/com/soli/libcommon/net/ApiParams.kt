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



    /**
    //     * 获取几位的随机数
    //     *
    //     * @param bit
    //     * @return
    //     */
//    private String getRandomNum(int bit) {
//        int maxNum = 36;
//        int i;
//        int count = 0;
//        char[] str = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
//                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
//                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
//        StringBuffer pwd = new StringBuffer();
//        Random r = new Random();
//        while (count < bit) {
//            i = Math.abs(r.nextInt(maxNum));
//            if (i >= 0 && i < str.length) {
//                pwd.append(str[i]);
//                count++;
//            }
//        }
//        return pwd.toString();
//    }
//
//    /**
//     * 判断字符串中是否有中文
//     *
//     * @param str
//     * @return
//     */
//    private boolean isContainChinese(String str) {
//        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher m = p.matcher(str);
//        return m.find();
//    }
//
//    /**
//     * 添加参数
//     * 授权登录接口和其他接口方式区分开，因为方式不一样
//     *
//     * @param url    请求的接口
//     * @param method 请求的方法
//     */
//    public ApiParams getParams(String url, String method) {
//
//        if (AuthInfo.isEmpty()) {
//            //更新token失败后，会导致本地的token 和pubkey 为空，所以这里要判断一下，然后在拦截器里做处理
//            put("needReRSA", true);
//            return this;
//        }
//
//        remove("needReRSA");
//
//        put("_authOnce", getRandomNum(8));
//        put("action", url);
//        put("method", method);
//
//        remove("q");
//        String request = RSAUtils.encryptDataByPublickey(getParams(true, method), AuthInfo.getPubKey());
//        clear();
//        put("q", request);
//        return this;
//    }
//
//    /**
//     * 获取websocket要穿的打包参数
//     *
//     * @param url
//     * @return
//     */
//    public ApiParams getWebSocketParams(String url, boolean postAsJson) {
//        getParams(url, "POST");
//        Object q = get("q");
//        if (q != null) {
//            String params = q.toString();
//            clear();
//            put("token", getRandomNum(32));
//            put("__token", AuthInfo.getToken());
//            put("params", params);
//            put("action", url);
//            if (postAsJson)
//                put("contentType", "application/json");
//            put("method", "POST");
//            put("uid", UserInfoBean.getUserId());
//        }
//        return this;
//    }
//
//    /**
//     * 获取json字符串参数
//     *
//     * @return
//     */
//    public String getJsonParams() {
//        return getParams(true, "POST");
//    }
//
//    /**
//     * @param getJsonStr
//     * @param requestModel
//     * @return
//     */
//    private String getParams(boolean getJsonStr, String requestModel) {
//        try {
//            JSONObject json = new JSONObject();
//            StringBuffer sp = new StringBuffer();
//            Iterator<Entry<String, Object>> iter = this.entrySet().iterator();
//            while (iter.hasNext()) {
//                Entry<String, Object> entry = iter.next();
//                String key = entry.getKey();
//                Object val = entry.getValue();
//
//                if (val == null)
//                    continue;
//
//                //中文编码一下
//                if (requestModel.equals("GET") && val instanceof String && isContainChinese((String) val)) {
//                    val = URLEncoder.encode((String) val, "UTF-8");
//                }
//
//
//                sp.append(key).append("=").append(String.valueOf(val)).append("&");
//                if (getJsonStr) {
//                    json.put(key, val);
//                }
//            }
//
//            // 删除最后一个&
//            if (sp.length() > 1)
//                sp.deleteCharAt(sp.length() - 1);
//
//            String authkey = Utils.INSTANCE.MD5(sp.toString());
//
//            if (getJsonStr) {
//                json.put("_authKey", authkey);
//            }
//
//            String result = getJsonStr ? json.toString() : (sp.toString() + "&_authKey=" + authkey);
//            if (Constant.Debug)
//                MLog.e("网络请求参数", result);
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "";
//    }

}
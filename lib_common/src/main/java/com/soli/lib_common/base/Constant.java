package com.soli.lib_common.base;

import android.content.Context;

/**
 * @author Soli
 * @Time 18-5-15 上午11:18
 */
public class Constant {

    public static boolean Debug;

    /**
     * 服务器地址
     */
    public static String webServer = "";

    private static Context ctx;

    /**
     * @param context
     */
    public static void init(Context context) {
        ctx = context.getApplicationContext();
    }

    public static Context getContext() {
        return ctx;
    }
}

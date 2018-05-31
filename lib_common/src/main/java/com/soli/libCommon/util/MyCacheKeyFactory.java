package com.soli.libCommon.util;

import android.net.Uri;

import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;

/**
 * Created by Soli on 2016/8/24.
 */
public class MyCacheKeyFactory extends DefaultCacheKeyFactory {

    private static MyCacheKeyFactory sInstance = null;

    protected MyCacheKeyFactory() {
    }

    public static synchronized MyCacheKeyFactory getInstance() {
        if (sInstance == null) {
            sInstance = new MyCacheKeyFactory();
        }
        return sInstance;
    }

    @Override
    protected Uri getCacheKeySourceUri(Uri sourceUri) {
        return Uri.parse(Utils.MD5(sourceUri.toString()));
    }
}

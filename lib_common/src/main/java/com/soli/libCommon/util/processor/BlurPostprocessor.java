package com.soli.libCommon.util.processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.soli.libCommon.util.processor.internal.FastBlur;
import com.soli.libCommon.util.processor.internal.RSBlur;

/**
 * Created by Soli on 2016/11/4.
 */

public class BlurPostprocessor extends BasePostprocessor {
    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private Context context;
    private int radius;
    private int sampling;

    private String key;


    public BlurPostprocessor(Context context, String mkey) {
        this(context, MAX_RADIUS, DEFAULT_DOWN_SAMPLING, mkey);
    }

    public BlurPostprocessor(Context context, int radius) {
        this(context, radius, DEFAULT_DOWN_SAMPLING, "");
    }

    public BlurPostprocessor(Context context, int radius, String mkey) {
        this(context, radius, DEFAULT_DOWN_SAMPLING, mkey);
    }

    /**
     * @param context
     * @param radius
     * @param sampling
     * @param key
     */
    public BlurPostprocessor(Context context, int radius, int sampling, String key) {
        this.context = context.getApplicationContext();
        this.radius = radius;
        this.sampling = sampling;
        this.key = key;
    }

    @Override
    public void process(Bitmap dest, Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;

        Bitmap blurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(blurredBitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                blurredBitmap = RSBlur.blur(context, blurredBitmap, radius);
            } catch (android.renderscript.RSRuntimeException e) {
                blurredBitmap = FastBlur.blur(blurredBitmap, radius, true);
            }
        } else {
            blurredBitmap = FastBlur.blur(blurredBitmap, radius, true);
        }

        Bitmap scaledBitmap =
                Bitmap.createScaledBitmap(blurredBitmap, dest.getWidth(), dest.getHeight(), true);
        blurredBitmap.recycle();
        blurredBitmap = null;

        super.process(dest, scaledBitmap);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("radius=" + radius + "sampling=" + sampling + key);
    }
}

package com.soli.libCommon.util.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;

/**
 * Created by Soli on 2016/10/25.
 */

public class GrayPostprocessor extends BasePostprocessor {

    private String key;

    public GrayPostprocessor(String mkey) {
        key = mkey;
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    //实现灰色的最好方式
    @Override
    public void process(Bitmap dest, Bitmap source) {
        super.process(dest, source);
        Canvas canvas = new Canvas(dest);
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(0f);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(saturation));
        canvas.drawBitmap(source, 0, 0, paint);
    }
//
//    @Override
//    public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
//        CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight());
//        try {
//            Bitmap destBitmap = bitmapRef.get();
//            int width = sourceBitmap.getWidth();         //获取位图的宽
//            int height = sourceBitmap.getHeight();       //获取位图的高
//
//            int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
//            sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//            int alpha = 0xFF << 24;
//            for (int i = 0; i < height; i++) {
//                for (int j = 0; j < width; j++) {
//                    int grey = pixels[width * i + j];
//
//                    int red = ((grey & 0x00FF0000) >> 16);
//                    int green = ((grey & 0x0000FF00) >> 8);
//                    int blue = (grey & 0x000000FF);
//
//                    grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
//                    grey = alpha | (grey << 16) | (grey << 8) | grey;
//                    pixels[width * i + j] = grey;
//                }
//            }
//            destBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            return CloseableReference.cloneOrNull(bitmapRef);
//        } finally {
//            CloseableReference.closeSafely(bitmapRef);
//        }
//    }

    @Override
    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey(key);
    }
}

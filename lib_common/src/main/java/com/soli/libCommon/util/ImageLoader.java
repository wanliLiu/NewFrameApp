package com.soli.libCommon.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.soli.libCommon.base.Constant;

/**
 *
 */
public class ImageLoader {

    /**
     * 直接加载原图
     *
     * @param image
     * @param url
     */
    public static void loadImageOrignal(SimpleDraweeView image, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(ImageRequest.fromUri(url))
                .build();
        image.setController(draweeController);
    }

    /**
     * @param image
     * @param url
     */
    public static void loadImage(SimpleDraweeView image, String url) {

        int width = image.getWidth();
        int height = image.getHeight();

        loadImage(image, url, width, height);
    }

    /**
     * @param image
     * @param url
     * @param width
     * @param height
     */
    public static void loadImage(SimpleDraweeView image, String url, int width, int height) {

        if (TextUtils.isEmpty(url)) {
            return;
        }

        int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100.0f, Constant.getContext().getResources().getDisplayMetrics());

        if (width <= 0) {
            width = defaultSize;
        }
        if (height <= 0) {
            height = defaultSize;
        }

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(width, height))
                .setRotationOptions(RotationOptions.autoRotate())
                .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .build();
        image.setController(draweeController);

    }

    /**
     * 加载本地图片（drawable,mipmap图片）
     *
     * @param simpleDraweeView
     * @param id
     */
    public static void loadResPic(SimpleDraweeView simpleDraweeView, int id) {
        Uri uri = Uri.parse("res://" + Constant.getContext().getPackageName() + "/" + id);
        simpleDraweeView.setImageURI(uri);
    }

}

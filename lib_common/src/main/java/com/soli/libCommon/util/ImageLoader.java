package com.soli.libCommon.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.soli.libCommon.base.Constant;

/**
 * 利用Fresco 加载图片
 *
 * @author Soli
 * @Time 18-5-31 下午4:53
 */
public class ImageLoader {

    /**
     * @param image
     * @param path
     * @param width
     * @param height
     */
    public static void loadImageByPath(SimpleDraweeView image, String path, int width, int height) {
        loadImageByPath(image, path, width, height, false);
    }

    /**
     * 加载本地图片
     */
    public static void loadImageByPath(SimpleDraweeView image, String path, int width, int height, boolean isGifAutoPlay) {
        loadImageByPath(image, path, width, height, isGifAutoPlay, false);
    }

    /**
     * @param image
     * @param path
     * @param width
     * @param height
     * @param isGifAutoPlay
     * @param isDealNight
     */
    public static void loadImageByPath(SimpleDraweeView image, String path, int width, int height, boolean isGifAutoPlay, boolean isDealNight) {
        loadImage(image, "file://" + path, isGifAutoPlay, width, height, isDealNight, null, null);
    }

    /**
     * 直接加载原图
     *
     * @param image
     * @param url
     */
    public static void loadImageOrignal(SimpleDraweeView image, String url) {

        if (image == null || TextUtils.isEmpty(url))
            return;

        ImageRequestBuilder imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));

//        if (ThemeUtil.INSTANCE.isThemeNight()) {
//            imageRequest.setPostprocessor(new NightPostprocessor(Utils.INSTANCE.MD5(url)));
//        }

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest.build())
                .build();
        image.setController(draweeController);
    }


    /**
     * @param image
     * @param url
     */
    public static void loadImageGif(SimpleDraweeView image, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(ImageRequest.fromUri(url))
                .setAutoPlayAnimations(true)
                .build();
        image.setController(draweeController);
    }

    /**
     * @param image
     * @param url
     */
    public static void loadImage(SimpleDraweeView image, String url) {

        if (image == null) return;

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
        loadImage(image, url, false, width, height, true, null, null);
    }

    public static void loadImage(SimpleDraweeView image, String url, int width, int height, ControllerListener controllerListener) {
        loadImage(image, url, false, width, height, true, null, controllerListener);
    }

    /**
     * @param image
     * @param url
     * @param isAutoPlay
     * @param width
     * @param height
     * @param dealNight
     * @param processor
     * @param controllerListener
     */
    public static void loadImage(SimpleDraweeView image,
                                 String url,
                                 boolean isAutoPlay,
                                 int width,
                                 int height,
                                 boolean dealNight,
                                 BasePostprocessor processor,
                                 ControllerListener controllerListener) {

        if (image == null) return;

        if (TextUtils.isEmpty(url)) {
            return;
        }

        MLog.d("图片加载", url);

        int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100.0f, Constant.getContext().getResources().getDisplayMetrics());

        if (width <= 0) {
            width = defaultSize;
        }
        if (height <= 0) {
            height = defaultSize;
        }

        ImageRequestBuilder imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(width, height))
                .setRotationOptions(RotationOptions.autoRotate());

        if (processor != null) {
            imageRequest.setPostprocessor(processor);
        }

//        if (dealNight && ThemeUtil.INSTANCE.isThemeNight()) {
//            imageRequest.setPostprocessor(new NightPostprocessor(Utils.INSTANCE.MD5(url)));
//        }

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest.build())
                .setOldController(image.getController())
                .setTapToRetryEnabled(true)
                .setAutoPlayAnimations(isAutoPlay)
                .setControllerListener(controllerListener)
                .build();
        image.setController(draweeController);
    }


    /**
     * 加载本地Res图片（drawable,mipmap图片）
     *
     * @param image
     * @param id
     */
    public static void loadResPic(SimpleDraweeView image, int id) {
        Uri uri = Uri.parse("res://" + Constant.getContext().getPackageName() + "/" + id);
        image.setImageURI(uri);
    }


    /**
     * 获取bitmap
     */
    public static void getBitmapByUrl(String url, int width, int height, BaseBitmapDataSubscriber dataSubscriber) {


        if (TextUtils.isEmpty(url)) {
            return;
        }

        MLog.d("图片加载", url);

        int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100.0f, Constant.getContext().getResources().getDisplayMetrics());

        if (width <= 0) {
            width = defaultSize;
        }
        if (height <= 0) {
            height = defaultSize;
        }

        ImageRequestBuilder imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(width, height))
                .setRotationOptions(RotationOptions.autoRotate());


//        if (ThemeUtil.INSTANCE.isThemeNight()) {
//            imageRequest.setPostprocessor(new NightPostprocessor(Utils.INSTANCE.MD5(url)));
//        }

        DataSource<CloseableReference<CloseableImage>> dataSource = ImagePipelineFactory.getInstance()
                .getImagePipeline()
                .fetchDecodedImage(imageRequest.build(), null);

        dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());

    }

}

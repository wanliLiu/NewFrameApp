package com.soli.libcommon.util

import android.net.Uri
import android.text.TextUtils
import android.util.TypedValue
import com.facebook.common.executors.UiThreadImmediateExecutorService
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.core.ImagePipelineFactory
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.BasePostprocessor
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.soli.libcommon.base.Constant

/**
 * 利用Fresco 加载图片
 *
 * @author Soli
 * @Time 18-5-31 下午4:53
 */
object ImageLoader {

    /**
     * @param image
     * @param path
     * @param width
     * @param height
     * @param isGifAutoPlay
     * @param isDealNight
     */
    fun loadImageByPath(
        image: SimpleDraweeView?,
        path: String?,
        width: Int = 0,
        height: Int = 0,
        isGifAutoPlay: Boolean = false,
        isDealNight: Boolean = false
    ) {
        loadImage(image, "file://$path",width,height,isGifAutoPlay,isDealNight)
    }

    /**
     * 直接加载原图
     *
     * @param image
     * @param url
     */
    fun loadImageOrignal(image: SimpleDraweeView?, url: String?) {

        if (image == null || TextUtils.isEmpty(url))
            return

        val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))

        //        if (ThemeUtil.INSTANCE.isThemeNight()) {
        //            imageRequest.setPostprocessor(new NightPostprocessor(Utils.INSTANCE.MD5(url)));
        //        }

        val draweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest.build())
            .build()
        image.controller = draweeController
    }


    /**
     * @param image
     * @param url
     */
    fun loadImageGif(image: SimpleDraweeView?, url: String?) {
        val draweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(ImageRequest.fromUri(url))
            .setAutoPlayAnimations(true)
            .build()
        image?.controller = draweeController
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
    fun loadImage(
        image: SimpleDraweeView?,
        url: String?,
        width: Int = 0,
        height: Int = 0,
        isAutoPlay: Boolean = true,
        dealNight: Boolean = false,
        processor: BasePostprocessor? = null,
        controllerListener: ControllerListener<in ImageInfo>? = null
    ) {
        var width = width
        var height = height

        if (image == null) return

        if (TextUtils.isEmpty(url)) {
            return
        }

        MLog.d("图片加载", url)

        val defaultSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100.0f,
            Constant.getContext().resources.displayMetrics
        ).toInt()

        if (width <= 0) {
            width = defaultSize
        }
        if (height <= 0) {
            height = defaultSize
        }

        val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
            .setLocalThumbnailPreviewsEnabled(true)
            .setResizeOptions(ResizeOptions(width, height))
            .setRotationOptions(RotationOptions.autoRotate())

        if (processor != null) {
            imageRequest.postprocessor = processor
        }

        //        if (dealNight && ThemeUtil.INSTANCE.isThemeNight()) {
        //            imageRequest.setPostprocessor(new NightPostprocessor(Utils.INSTANCE.MD5(url)));
        //        }

        val draweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(imageRequest.build())
            .setOldController(image.controller)
            .setTapToRetryEnabled(true)
            .setAutoPlayAnimations(isAutoPlay)
            .setControllerListener(controllerListener)
            .build()
        image.controller = draweeController
    }


    /**
     * 加载本地Res图片（drawable,mipmap图片）
     *
     * @param image
     * @param id
     */
    fun loadResPic(image: SimpleDraweeView, id: Int) {
        val uri = Uri.parse("res://" + Constant.getContext().packageName + "/" + id)
        image.setImageURI(uri,image.context)
    }

}


package com.soli.libcommon.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.soli.libcommon.R

/**
 *
 * @author Soli
 * @Time 2018/10/31 18:00
 *
 * <com.facebook.drawee.view.SimpleDraweeView
 *   android:id="@+id/my_image_view"
 *   android:layout_width="20dp"
 *   android:layout_height="20dp"
 *   fresco:fadeDuration="300"
 *   fresco:actualImageScaleType="focusCrop"
 *   fresco:placeholderImage="@color/wait_color"
 *   fresco:placeholderImageScaleType="fitCenter"
 *   fresco:failureImage="@drawable/error"
 *   fresco:failureImageScaleType="centerInside"
 *   fresco:retryImage="@drawable/retrying"
 *   fresco:retryImageScaleType="centerCrop"
 *   fresco:progressBarImage="@drawable/progress_bar"
 *   fresco:progressBarImageScaleType="centerInside"
 *   fresco:progressBarAutoRotateInterval="1000"
 *   fresco:backgroundImage="@color/blue"
 *   fresco:overlayImage="@drawable/watermark"
 *   fresco:pressedStateOverlayImage="@color/red"
 *   fresco:roundAsCircle="false"
 *   fresco:roundedCornerRadius="1dp"
 *   fresco:roundTopLeft="true"
 *   fresco:roundTopRight="false"
 *   fresco:roundBottomLeft="false"
 *   fresco:roundBottomRight="true"
 *   fresco:roundTopStart="false"
 *   fresco:roundTopEnd="false"
 *   fresco:roundBottomStart="false"
 *   fresco:roundBottomEnd="false"
 *   fresco:roundWithOverlayColor="@color/corner_color"
 *   fresco:roundingBorderWidth="2dp"
 *   fresco:roundingBorderColor="@color/border_color"
 *  />
 */
open class FrescoImageView : BaseDraweeView {

    private val defaultId = R.drawable.default_loading_back_8dp_ng

    constructor(ctx: Context, hierarchy: GenericDraweeHierarchy) : super(ctx, hierarchy)

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int) : super(ctx, attrs, defStyle)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        ctx,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        initHierarchy(defaultId)
    }


    /**
     * @param resId placeHolder
     * @param fromCode 是否是代码直接设置，默认是xml过来的 attrs
     */
    private fun initHierarchy(resId: Int, fromCode: Boolean = false) {

        if (resId == 0)
            return

        hierarchy.apply {

            if (hierarchyBuilder!!.actualImageScaleType != defaultScaleType)
                actualImageScaleType = defaultScaleType //ScalingUtils.ScaleType.CENTER_CROP

            if (hierarchyBuilder!!.fadeDuration != defaultFadeDuration)
                fadeDuration = defaultFadeDuration

            if (hierarchyBuilder!!.placeholderImage == null || fromCode)
                setPlaceholderImage(ContextCompat.getDrawable(context, resId), defaultScaleType)

            if (hierarchyBuilder!!.failureImage == null || fromCode)
                setFailureImage(ContextCompat.getDrawable(context, resId), defaultScaleType)

            hierarchy = this
        }
    }
    /**
     *
     */
    fun setPlaceholderImage(resId: Int) {
        initHierarchy(resId, true)
    }
}
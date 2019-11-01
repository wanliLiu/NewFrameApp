package com.soli.libcommon.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.util.AttributeSet
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.GenericDraweeHierarchyInflater
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.systrace.FrescoSystrace
/**
 *
 * @author Soli
 * @Time 2018/11/2 09:52
 */
open class BaseDraweeView : SimpleDraweeView {

    //默认scaletype
    val defaultScaleType: ScalingUtils.ScaleType
        get() = ScalingUtils.ScaleType.CENTER_CROP

    val defaultFadeDuration: Int
        get() = 500

    open var hierarchyBuilder: GenericDraweeHierarchyBuilder? = null

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

    override fun inflateHierarchy(context: Context?, attrs: AttributeSet?) {
//        super.inflateHierarchy(context, attrs)
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("GenericDraweeView#inflateHierarchy")
        }
        hierarchyBuilder = GenericDraweeHierarchyInflater.inflateBuilder(context, attrs)
        aspectRatio = hierarchyBuilder!!.desiredAspectRatio
        hierarchy = hierarchyBuilder!!.build()

        val roundingParams = getRoundingParams()
        //统一处理gif 圆形 问题
//        roundingParams.roundingMethod = RoundingParams.RoundingMethod.OVERLAY_COLOR
//        roundingParams.overlayColor = SkinCompatResources.getColor(context, R.color.B2)
        roundingParams.scaleDownInsideBorders = true

        hierarchy.roundingParams = roundingParams

        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection()
        }
    }

    /**
     *
     */
    fun getRoundingParams(): RoundingParams {
        var roundingParams = hierarchy.roundingParams
        if (roundingParams == null) {
            roundingParams = RoundingParams()
        }
        return roundingParams
    }

    /**
     *
     */
    fun setCornersRadius(radius: Float) {
        val roundingParams = getRoundingParams()
        roundingParams.setCornersRadius(radius)
        hierarchy.roundingParams = roundingParams
    }

    /**
     * 设置边界宽带和原色
     */
    fun setBorder(borderWidth: Float, color: Int) {
        val roundingParams = getRoundingParams()
        roundingParams.borderWidth = borderWidth
        roundingParams.borderColor = color
        hierarchy.roundingParams = roundingParams
    }

    /**
     * 设置边界颜色
     */
    fun setBorder(color: Int) {
        val roundingParams = getRoundingParams()
        roundingParams.borderColor = color
        hierarchy.roundingParams = roundingParams
    }

    /**
     *
     */
    fun setActualScaleType(scaleType: ScalingUtils.ScaleType, focusPoint: PointF? = null) {
        hierarchy?.apply {
            actualImageScaleType = scaleType
            if (focusPoint != null)
                setActualImageFocusPoint(focusPoint)
            hierarchy = this
        }
    }
}
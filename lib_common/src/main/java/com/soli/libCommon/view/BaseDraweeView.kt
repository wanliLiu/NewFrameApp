package com.soli.libCommon.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.GenericDraweeHierarchyInflater
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
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection()
        }
    }
}
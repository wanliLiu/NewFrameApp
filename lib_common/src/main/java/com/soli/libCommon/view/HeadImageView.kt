package com.soli.libCommon.view

import android.content.Context
import android.util.AttributeSet
import com.facebook.drawee.generic.RoundingParams
import com.soli.libCommon.R
import com.soli.libCommon.util.ImageLoader

/**
 *
 * @author Soli
 * @Time 2018/11/2 09:17
 */
open class HeadImageView : BaseDraweeView {

    //默认网络加载的裁剪大小
    private val defaultSize: Int = context.resources.getDimensionPixelSize(R.dimen.default_display_avatar_size)
    private val defaultAvatar = R.drawable.icon_lavator

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int) : super(ctx, attrs, defStyle)

    init {
        initHierarchy(defaultAvatar)
    }

    /**
     *
     */
    fun setPlaceholderImage(resId: Int) {
        initHierarchy(resId, true)
    }


    /**
     * @param resId placeHolder
     * @param fromCode 是否是代码直接设置，默认是xml过来的 attrs
     */
    private fun initHierarchy(resId: Int, fromCode: Boolean = false) {

        if (resId == 0)
            return

        hierarchy.apply {

            if (hierarchyBuilder!!.fadeDuration != defaultFadeDuration)
                fadeDuration = defaultFadeDuration

            if (hierarchyBuilder!!.actualImageScaleType != defaultScaleType)
                actualImageScaleType = defaultScaleType


            var mParams = roundingParams
            if (mParams == null)
                mParams = RoundingParams()
            mParams.roundAsCircle = true
            roundingParams = mParams


            if (hierarchyBuilder!!.placeholderImage == null || fromCode)
                setPlaceholderImage(resId)

            if (hierarchyBuilder!!.failureImage == null || fromCode)
                setFailureImage(resId)

            hierarchy = this
        }
    }

    /**
     *  默认头像 默认网络加载裁剪大小
     */
    fun loadImage(url: String?) {
        ImageLoader.loadImage(this, url, defaultSize, defaultSize)
    }

    /**
     * 加载图片
     * @url 图片全地址
     * @defaultResId 展位图
     */
    open fun doLoadImage(url: String?, resId: Int = 0, thumbSize: Int = defaultSize) {
        setPlaceholderImage(resId)
        ImageLoader.loadImage(this, url, thumbSize, thumbSize)
    }

    /**
     *
     */
    fun loadImageByPaht(url: String?) {
        ImageLoader.loadImageByPath(this, url, defaultSize, defaultSize)
    }

    /**
     * 加载本地图片
     * @url 图片全地址
     * @defaultResId 展位图
     */
    fun doLoadImageByPath(url: String?, resId: Int = 0, thumbSize: Int = defaultSize) {
        setPlaceholderImage(resId)
        ImageLoader.loadImageByPath(this, url, thumbSize, thumbSize)
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
     *
     */
    private fun getRoundingParams(): RoundingParams {
        var roundingParams = hierarchy.roundingParams
        if (roundingParams == null) {
            roundingParams = RoundingParams()
        }
        return roundingParams
    }

}
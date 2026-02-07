package com.soli.libcommon.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.soli.libcommon.R

/**
 *
 * @author Soli
 * @Time 2020/4/21 09:58
 */
class SvgImageView : AppCompatImageView {

    private val INVALID_ID = 0

    private var svgColor = INVALID_ID

    //    系统使用了这个，做了处理( mDrawable.mutate())，所以不行
    private var androidTint = INVALID_ID
    private var tint = INVALID_ID
    //    系统使用了这个，做了处理( mDrawable.mutate())，所以不行

    private var mSrcResId = INVALID_ID
    private var mSrcCompatResId = INVALID_ID


    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    ) {
        loadFromAttributes(attrs, defStyleAttr)
    }


    /**
     *
     */
    private fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.apply {
            var a: TypedArray? = null
            try {
                a = context.obtainStyledAttributes(this, R.styleable.SvgImageView, defStyleAttr, 0)
                svgColor = a.getResourceId(R.styleable.SvgImageView_svg_color, INVALID_ID)
//                tint = a.getResourceId(R.styleable.SvgImageView_tint, INVALID_ID)
//                androidTint = a.getResourceId(R.styleable.SvgImageView_android_tint, INVALID_ID)
                mSrcResId = a.getResourceId(R.styleable.SvgImageView_android_src, INVALID_ID)
                mSrcCompatResId = a.getResourceId(R.styleable.SvgImageView_srcCompat, INVALID_ID)
            } finally {
                a?.recycle()
            }
            applyChange()
        }
    }


    private fun checkResourceId(resId: Int): Int {
        return if (resId.toHexString().startsWith("1")) INVALID_ID else resId
    }


    /**
     *
     */
    private fun setSvgColor(drawableCompat: VectorDrawableCompat) {
        when {
            svgColor != INVALID_ID -> drawableCompat.setTint(
                ContextCompat.getColor(
                    context,
                    svgColor
                )
            )
            tint != INVALID_ID -> drawableCompat.setTint(ContextCompat.getColor(context, tint))
            androidTint != INVALID_ID -> drawableCompat.setTint(
                ContextCompat.getColor(context, androidTint)
            )
        }
    }

    /**
     *
     */
    private fun applyChange() {

        val theme = context.theme

        val drawable: VectorDrawableCompat?
        mSrcCompatResId = checkResourceId(mSrcCompatResId)
        if (mSrcCompatResId != INVALID_ID) {
            drawable = VectorDrawableCompat.create(resources, mSrcCompatResId, theme)
        } else {
            mSrcResId = checkResourceId(mSrcResId)
            if (mSrcResId == INVALID_ID) {
                return
            }
            drawable = VectorDrawableCompat.create(resources, mSrcResId, theme)
        }

        if (drawable != null) {
            setSvgColor(drawable)
            setImageDrawable(drawable)
        }
    }

    /**
     *
     */
    private fun setInnerImageResource(
        resId: Int, color: Int = when {
            svgColor != INVALID_ID -> svgColor
            tint != INVALID_ID -> tint
            androidTint != INVALID_ID -> androidTint
            else -> INVALID_ID
        }
    ) {
        mSrcCompatResId = resId
        svgColor = color
        applyChange()
    }

    override fun setImageResource(resId: Int) {
        setInnerImageResource(resId)
    }

    /**
     *
     */
    fun setImageColor(colorId: Int) {
        setInnerImageResource(mSrcCompatResId, colorId)
    }

    /**
     *
     */
    fun setImageResource(@DrawableRes resId: Int, colorId: Int) {
        setInnerImageResource(resId, colorId)
    }
}
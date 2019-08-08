package com.soli.libcommon.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.widget.ImageView
import com.soli.libcommon.R

/*
 * @author soli
 * @Time 2018/12/8 11:38
 */
class PullLoadingImageView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ImageView(context, attrs, defStyleAttr) {

    var isAutoAnimation = true

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    init {
        setImageResource(R.drawable.pull_loading)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (drawable is AnimationDrawable && isAutoAnimation) {
            (drawable as AnimationDrawable).start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (drawable is AnimationDrawable && isAutoAnimation) {
            (drawable as AnimationDrawable).stop()
        }
    }


    fun startAnim() {
        if (drawable is AnimationDrawable) {
            (drawable as AnimationDrawable).apply {
                if (!isRunning)
                    start()
            }
        }
    }


    fun stopAnim() {
        if (drawable is AnimationDrawable) {
            (drawable as AnimationDrawable).apply {
                if (isRunning)
                    stop()
            }
        }
    }
}
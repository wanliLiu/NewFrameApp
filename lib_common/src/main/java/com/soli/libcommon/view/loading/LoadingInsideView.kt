package com.soli.libcommon.view.loading

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.soli.libcommon.R

/*
 * @author soli
 * @Time 2018/12/16 23:16
 */
class LoadingInsideView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val ctx = context
    private val loadingRootView: FrameLayout
    private val loadingImageView: ProgressLoadingImageView

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)


    init {
        View.inflate(ctx, R.layout.loding_inside_top, this)
        loadingRootView = findViewById(R.id.loadingRootView)
        loadingImageView = findViewById(R.id.loadingImageView)

        showAsCenter()
    }

    /**
     *
     */
    fun setloadingBackgroundResource(resId: Int) {
        loadingRootView.setBackgroundResource(resId)
    }

    /**
     *
     */
    fun setloadingBackgroundColor(colorId: Int) {
        loadingRootView.setBackgroundColor(colorId)
    }

    /**
     *
     */
    fun showAsCenter() {
        (loadingImageView.layoutParams as LayoutParams).apply {
            gravity = Gravity.CENTER
            topMargin = 0
            leftMargin = 0
            rightMargin = 0
            bottomMargin = 0

            loadingImageView.layoutParams = this
        }
    }

    fun showAsTop_more() {
        showAsTop(ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height) * 3)
    }

    /**
     *
     */
    fun showAsTop(topOffset: Int = ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height)) {
        (loadingImageView.layoutParams as LayoutParams).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = topOffset
            leftMargin = 0
            rightMargin = 0
            bottomMargin = 0

            loadingImageView.layoutParams = this
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility) {
            View.GONE, View.INVISIBLE -> loadingImageView.stopAnim()
            View.VISIBLE -> loadingImageView.startAnim()
        }
    }
}
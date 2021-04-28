package com.soli.libcommon.view.loading

import android.content.Context
import android.util.AttributeSet

/*
 * @author soli
 * @Time 2018/12/8 12:12
 */
open class PullLoadingImageView : StrongSVGAImageView {


    private var isAutoAnimation = true

    //发现在progress 的时候，stepToFrame 为false的时候，后面不会动
    protected var initStartPlay = false

    //主要的那一帧
    private val perfectFrame = 30

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {

        clearsAfterStop = false
        fillMode = FillMode.Backward

        waitParseSvgaFile(getLoadingPath()) { success, mvideoItem ->
            if (success) {
                setVideoItem(mvideoItem)
                resetDefault()
            }
        }
    }


    fun resetDefault(needDo: Boolean = false) {
        post {
            if (!flagStop || needDo)
                stepToFrame(perfectFrame, initStartPlay)
        }
    }


    fun setAutoAnimation(autoAnimation: Boolean) {
        isAutoAnimation = autoAnimation
    }

    open fun getLoadingPath(): String = "svga/pull_loading.svga"

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAutoAnimation) {
            startAnim()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isAutoAnimation) {
            stopAnim()
        }
    }

    override fun startAnim() {
        if (!isAnimating) {
            //直接从某帧继续走
            checkHaveDrawabe { stepToFrame(perfectFrame, true) }
        }
    }
}
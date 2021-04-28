package com.soli.libcommon.view.loading

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAParser.Companion.shareParser
import com.opensource.svgaplayer.SVGAParser.ParseCompletion
import com.opensource.svgaplayer.SVGAVideoEntity
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.MLog

/**
 * Created by Android Studio.
 * User: 陈杜阳
 * Date: 2020/5/8
 * Time: 2:51 PM
 *
 * @author Soli 修改
 */
open class StrongSVGAImageView : SVGAImageView {

    protected val parser: SVGAParser = shareParser()

    private var currentAnimationPath = ""

    private val Tag = "CustomSVGAImageView"

    protected var flagStop = false

    companion object {
        //保存每个path解析的数据，防止反复解析
        private val videoEntity = mutableMapOf<String, SVGAVideoEntity>()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        clearsAfterDetached  = false

        parser.init(Constant.getContext)

        if (Constant.Debug) {
            callback = object : SVGACallback {
                override fun onFinished() = Unit
                override fun onPause() = Unit
                override fun onRepeat() = Unit

                override fun onStep(frame: Int, percentage: Double) {
                    MLog.e(
                        Tag,
                        "onStep-$currentAnimationPath frame-->$frame----->$percentage"
                    )
                }
            }
        }
    }

    /**
     * 处理svga文件的解析
     */
    protected fun waitParseSvgaFile(
        name: String,
        callback: (success: Boolean, mvideoItem: SVGAVideoEntity?) -> Unit
    ) {
        currentAnimationPath = name
        MLog.d(Tag, "savga加载地址：$currentAnimationPath")
        val item = videoEntity[name]
        if (item != null) {
            log("缓存有，直接用")
            callback(true, item)
        } else {
            log("缓存没有，开始解析")
            Thread {
                parser.decodeFromAssets(
                    name,
                    object : ParseCompletion {
                        override fun onComplete(videoItem: SVGAVideoEntity) {
                            videoEntity[name] = videoItem
                            log("svga 文件解析成功")
                            post { callback(true, videoItem) }
                        }

                        override fun onError() {
                            log("svga 解析失败")
                            post { callback(false, null) }
                        }
                    }
                )
            }.start()
        }
    }

    private fun log(msg: String) {
        MLog.d(Tag, "savga加载地址：$currentAnimationPath  $msg")
    }

    /**
     * 加载，但是不播放
     */
    fun playParepare(path: String) {
        playFromAssets(path, false)
    }

    /**
     *
     */
    fun playFromAssets(path: String, needPlay: Boolean = true) {
        waitParseSvgaFile(path) { success, mvideoItem ->
            if (success) {
                setVideoItem(mvideoItem)
                if (needPlay && !flagStop)
                    startAnimation()
            }
        }
    }

    /**
     * @param percentage [0-1]
     * */
    fun stepTo(path: String, percentage: Double, andPlay: Boolean) {
        waitParseSvgaFile(path) { success, mvideoItem ->
            if (success) {
                setVideoItem(mvideoItem)
                if (!flagStop) {
                    stepToPercentage(percentage, andPlay)
                }
            }
        }
    }

    /**
     * startAnim 开始，有时候还没有准备好
     */
    fun checkHaveDrawabe(callback: () -> Unit) {
        if (drawable == null && !TextUtils.isEmpty(currentAnimationPath)) {
            log("动画之前，没有设置drawale,开始重新解析")
            waitParseSvgaFile(currentAnimationPath) { success, mvideoItem ->
                if (success) {
                    log("动画之前，没有设置drawale,开始重新解析  解析成功")
                    setVideoItem(mvideoItem)
                    callback()
                }
            }
        } else callback()
    }

    /**
     *
     */
    open fun startAnim() {
        flagStop = false
        if (!isAnimating) {
            checkHaveDrawabe {
                post {
                    if (!flagStop) {
                        stepToFrame(0, true)
//                        startAnimation()
                        log("svga 真正开始动画")
                    }
                }
            }
        }
        log("svga 开始动画")
    }

    /**
     *
     */
    fun stopAnim() {
        flagStop = true
        if (isAnimating) {
            post {
                stopAnimation()
                log("svga 真正结束动画")
            }
        }
        log("svga 结束动画")
    }
}
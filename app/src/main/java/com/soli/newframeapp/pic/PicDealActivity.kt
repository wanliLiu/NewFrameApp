package com.soli.newframeapp.pic

import android.graphics.*
import android.widget.SeekBar
import com.soli.libCommon.base.BaseActivity
import com.soli.libCommon.util.FrescoUtil
import com.soli.libCommon.util.MLog
import com.soli.newframeapp.R
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_pic_deal.*

/**
 *
 * @author Soli
 * @Time 2018/11/12 09:38
 */
class PicDealActivity : BaseActivity() {

    private val MAX_VALUE = 255
    private val MID_VALUE = 127
    private var mLum = 1f
    private var mHue = 0f
    private var mSaturation = 1f

    private var bitmap: Bitmap? = null

    override fun getContentView() = R.layout.activity_pic_deal


    override fun initView() {

        title = "ColorMatrix"

        seek1.max = MAX_VALUE
        seek1.progress = MID_VALUE
        seek1.setOnSeekBarChangeListener(onSeekChange)

        seek2.max = MAX_VALUE
        seek2.progress = MID_VALUE
        seek2.setOnSeekBarChangeListener(onSeekChange)


        seek3.max = MAX_VALUE
        seek3.progress = MID_VALUE
        seek3.setOnSeekBarChangeListener(onSeekChange)

    }

    override fun initListener() {
    }

    override fun initData() {

        val tem =
            FrescoUtil.fetchBitmap("https://nim.nosdn.127.net/MTAxMTAwMg==/bmltYV81NDU5MjM4ODk3XzE1Mzk2ODIxMjg3NzBfOWY3YmZhYTYtNzczYy00YjBhLTg0MWEtYjYzMjVmMTIxZDhj?")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mb ->
                    bitmap = mb
                    imageview.setImageBitmap(bitmap)
                }

//        ImageLoader.loadResPic(headImage1, R.mipmap.icon_avatar_default)
        headImage.loadImage("https://nim.nosdn.127.net/MTAxMTAwMg==/bmltYV81NDU5MjM4ODk3XzE1Mzk2ODIxMjg3NzBfOWY3YmZhYTYtNzczYy00YjBhLTg0MWEtYjYzMjVmMTIxZDhj?")
    }

    private val onSeekChange = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            desc.text = when (seekBar?.id) {
                R.id.seek1 -> {
                    mHue = (progress - MID_VALUE) * 1.0f / MID_VALUE * 180
                    "色相-------$mHue"
                }
                R.id.seek2 -> {
                    mSaturation = progress * 1.0f / MID_VALUE
                    "饱和度-------$mSaturation"
                }
                R.id.seek3 -> {
                    mLum = progress * 1.0f / MID_VALUE
                    "亮度-------$mLum"
                }
                else -> {
                    "错误"
                }
            }
            MLog.e("调整", desc.text.toString())

            imageview.setImageBitmap(handleImageEffect(bitmap!!, mHue, mSaturation, mLum))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }


    /**
     *
     */
    private fun handleImageEffect(bitmap: Bitmap, hue: Float, saturation: Float, lum: Float): Bitmap {
        //传进来的bitmap默认不能修改  所以再创建一个bm
        val bm = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        //画布
        val canvas = Canvas(bm)
        //抗锯齿
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //颜色矩阵
        val hueMatrix = ColorMatrix()
        //修改色相   0 red 1 green 2 blue
        hueMatrix.setRotate(0, hue)
        hueMatrix.setRotate(1, hue)
        hueMatrix.setRotate(2, hue)
        //修改饱和度
        val saturationMatrix = ColorMatrix()
        saturationMatrix.setSaturation(saturation)
//        //修改亮度
        val lumMatrix = ColorMatrix()
        //r g b a    1 表示全不透明
        lumMatrix.setScale(lum, lum, lum, 1f)
//
//        //组合Matrix
        val imageMatrix = ColorMatrix()
        imageMatrix.postConcat(hueMatrix)
        imageMatrix.postConcat(saturationMatrix)
        imageMatrix.postConcat(lumMatrix)
        //为画笔设置颜色过滤器
        paint.colorFilter = ColorMatrixColorFilter(imageMatrix)
        //在canvas上照着bitmap画
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bm
    }
}
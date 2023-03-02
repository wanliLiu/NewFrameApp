package com.soli.newframeapp.pic

import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.SeekBar
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.libcommon.util.FrescoUtil
import com.soli.libcommon.util.MLog
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.ActivityPicDealBinding
import io.reactivex.android.schedulers.AndroidSchedulers


/**
 *
 * @author Soli
 * @Time 2018/11/12 09:38
 */
class PicDealFragment : BaseToolbarFragment<ActivityPicDealBinding>() {

    private val MAX_VALUE = 255
    private val MID_VALUE = 127

    private var mLum = 1f
    private var mHue = 0f
    private var mSaturation = 1f

    private var bitmap: Bitmap? = null

    override fun initView() {

        setTitle("ColorMatrix")

        binding.seek1.max = MAX_VALUE
        binding.seek1.progress = MID_VALUE
        binding.seek1.setOnSeekBarChangeListener(onSeekChange)

        binding.seek2.max = MAX_VALUE
        binding.seek2.progress = MID_VALUE
        binding.seek2.setOnSeekBarChangeListener(onSeekChange)


        binding.seek3.max = MAX_VALUE
        binding.seek3.progress = MID_VALUE
        binding.seek3.setOnSeekBarChangeListener(onSeekChange)

        binding.seek4.max = MAX_VALUE
        binding.seek4.progress = MAX_VALUE
        binding.blurCover.imageAlpha = 0
        binding.seek4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.blurCover.imageAlpha = MAX_VALUE - progress
                binding.desc.text = "毛玻璃效果"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun initListener() {
    }

    override fun initData() {

        val tem =
            FrescoUtil.fetchBitmap("https://nim.nosdn.127.net/MTAxMTAwMg==/bmltYV81NDU5MjM4ODk3XzE1Mzk2ODIxMjg3NzBfOWY3YmZhYTYtNzczYy00YjBhLTg0MWEtYjYzMjVmMTIxZDhj?")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mb ->
                    bitmap = mb
                    binding.imageview.setImageBitmap(bitmap)

                    binding.blurCover.setImageBitmap(bitmap)
                    binding.blurImageview.setImageBitmap(blur(bitmap!!, 25f))
                }

//        ImageLoader.loadResPic(headImage1, R.mipmap.icon_avatar_default)
        binding.headImage.loadImage("https://nim.nosdn.127.net/MTAxMTAwMg==/bmltYV81NDU5MjM4ODk3XzE1Mzk2ODIxMjg3NzBfOWY3YmZhYTYtNzczYy00YjBhLTg0MWEtYjYzMjVmMTIxZDhj?")
    }

    private val onSeekChange = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            binding.desc.text = when (seekBar?.id) {
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
            MLog.e("调整", binding.desc.text.toString())

            binding.imageview.setImageBitmap(handleImageEffect(bitmap!!, mHue, mSaturation, mLum))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }


    private fun blur(bitmap: Bitmap, radius: Float): Bitmap? {
        val output = Bitmap.createBitmap(bitmap) // 创建输出图片
        val rs = RenderScript.create(_mActivity) // 构建一个RenderScript对象
        val gaussianBlue = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)) //
        // 创建高斯模糊脚本
        val allIn = Allocation.createFromBitmap(rs, bitmap) // 开辟输入内存
        val allOut = Allocation.createFromBitmap(rs, output) // 开辟输出内存
        gaussianBlue.setRadius(radius) // 设置模糊半径，范围0f<radius<=25f
        gaussianBlue.setInput(allIn) // 设置输入内存
        gaussianBlue.forEach(allOut) // 模糊编码，并将内存填入输出内存
        allOut.copyTo(output) // 将输出内存编码为Bitmap，图片大小必须注意
        rs.destroy() // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
        return output
    }

    /**
     *
     */
    private fun handleImageEffect(
        bitmap: Bitmap,
        hue: Float,
        saturation: Float,
        lum: Float
    ): Bitmap {
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
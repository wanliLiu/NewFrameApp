package com.soli.libcommon.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import com.soli.libcommon.base.Constant

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Android大小单位转换工具类
 *
 * @author wader
 */
object ImageDeal {
    //如果图片文件大小大于250k才压缩，
    private val sizeBigThan = 250.00
    //高比宽大于OPGL_MAX_TEXTURE倍，我们认为就是长图
    private val OPGL_MAX_TEXTURE = 4

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation =
                exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        if (angle == 0) {
            return bitmap
        }
        // 旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 进行相应的计算
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private fun computeSampleSize(
        options: BitmapFactory.Options,
        minSideLength: Int, maxNumOfPixels: Int
    ): Int {
        val initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels)

        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }

        return roundedSize
    }

    /**
     * 进行相应的计算
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private fun computeInitialSampleSize(
        options: BitmapFactory.Options,
        minSideLength: Int, maxNumOfPixels: Int
    ): Int {
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()

        val lowerBound = if (maxNumOfPixels == -1)
            1
        else
            ceil(sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength == -1)
            128
        else
            min(floor(w / minSideLength), floor(h / minSideLength)).toInt()

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound
        }

        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }

    /**
     * 进行相应的计算
     *
     * @param dst
     * @param width
     * @param height
     * @return
     */
    fun getBitmapFromFile(dst: File?, width: Int, height: Int): Bitmap? {
        if (null != dst && dst.exists()) {
            var opts: BitmapFactory.Options? = null
            val degree = readPictureDegree(dst.absolutePath)
            if (width > 0 && height > 0) {
                opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = true
                BitmapFactory.decodeFile(dst.path, opts)
                // 计算图片缩放比例
                val minSideLength = Math.min(width, height)
                opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height)
                opts.inJustDecodeBounds = false
                opts.inInputShareable = true
                opts.inPurgeable = true
            }
            try {
                return rotaingImageView(degree, BitmapFactory.decodeFile(dst.path, opts))
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }

        }
        return null
    }


    /**
     * @param file
     * @return
     */
    fun getImageOpt(file: File?): BitmapFactory.Options? {
        if (file != null && file.exists()) {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, opts)
            return opts
        }

        return null
    }

    /**
     * @return
     */
    fun getDebugString(file: String): String {
        var temp = FileSizeUtil.getAutoFileOrFilesSize(file) + "----"

        val options = getImageOpt(File(file))
        if (options != null)
            temp += options.outWidth.toString() + "*" + options.outHeight

        return temp
    }

    /**
     * @param file
     * @return
     */
    fun getFilePicSize(file: String): IntArray? {
        var file = file
        if (file.startsWith("file://"))
            file = file.substring("file://".length)
        val options = getImageOpt(File(file))

        return if (options != null) {
            intArrayOf(options.outWidth, options.outHeight)
        } else null
    }

    /**
     * @param file
     * @return
     */
    fun isLargerPicture(file: String): Boolean {
        var file = file
        if (file.startsWith("file://"))
            file = file.substring("file://".length)
        val options = getImageOpt(File(file))
        return if (options != null) isLargerPicture(options.outWidth, options.outHeight) else false
    }

    /**
     * @param width
     * @param height
     * @return
     */
    fun isLargerPicture(width: Int, height: Int): Boolean {
        return height > width * OPGL_MAX_TEXTURE
    }

    /**
     * 上传图片统一压缩
     *
     * @param ctx
     * @param orignalPath 图片原地址
     * @return 压缩后的地址
     */
    private fun zipPictureToXX_XX(ctx: Context, orignalPath: String): String {

        if (orignalPath.endsWith(".gif"))
            return orignalPath

        var tempFile = orignalPath
        val degree = readPictureDegree(orignalPath)
        if (FileSizeUtil.getFileOrFilesSize(orignalPath, FileSizeUtil.SIZETYPE_KB) > sizeBigThan || degree > 0) {
            tempFile = FileUtil.getPicUploadTempPath(ctx, orignalPath)
            if (File(tempFile).exists()) {
                logDeug("图片压缩", "缓存已经有，不需要再压缩" + getDebugString(tempFile))
                return tempFile
            }
            var bm: Bitmap? = null
            try {
                bm = getBitmapFromFile(File(orignalPath), 720, 1280)
                val fos = FileOutputStream(File(tempFile))
                bm!!.compress(Bitmap.CompressFormat.JPEG, 95, fos)
            } catch (e: Exception) {
            } finally {
                bm?.recycle()
                logDeug("图片压缩", "图片压缩后大小" + getDebugString(tempFile))
            }
        } else {
            logDeug("图片压缩", "图片不需要压缩，直接上传")
        }

        return tempFile
    }

    /**
     * 相对来说比较高清的一种压缩方式
     *
     * @param ctx
     * @param orignalPath
     */
    fun compressPic(ctx: Context, orignalPath: String): String {
        if (orignalPath.endsWith(".gif"))
            return orignalPath
        logDeug("图片压缩", "图片原大小" + getDebugString(orignalPath))
        var tempFile = orignalPath
        val degree = readPictureDegree(orignalPath)
        if (FileSizeUtil.getFileOrFilesSize(orignalPath, FileSizeUtil.SIZETYPE_KB) > sizeBigThan || degree > 0) {
            tempFile = FileUtil.getPicUploadTempPath(ctx, orignalPath)
            if (File(tempFile).exists()) {
                logDeug("图片压缩", "缓存已经有，不需要再压缩" + getDebugString(tempFile))
                return tempFile
            }

            var bm: Bitmap? = null
            try {
                bm = getBitmapFromFile(File(orignalPath), 1080, 1920)
                val fos = FileOutputStream(File(tempFile))
                bm!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } catch (e: Exception) {
            } finally {
                bm?.recycle()
                logDeug("图片压缩", "图片压缩后大小" + getDebugString(tempFile))
            }
        } else {
            logDeug("图片压缩", "图片不需要压缩，直接上传")
        }

        return tempFile
    }

    /**
     * @param ctx
     * @param orignalPath
     * @param listener
     */
    fun compressPic(ctx: Context, zipType: Int, orignalPath: String, listener: ((String) -> Unit)?) {
        logDeug("图片压缩", "图片原大小" + getDebugString(orignalPath))
        if (orignalPath.endsWith(".gif")) {
            listener?.invoke(orignalPath)
            return
        }
        val degree = readPictureDegree(orignalPath)
        if (FileSizeUtil.getFileOrFilesSize(orignalPath, FileSizeUtil.SIZETYPE_KB) > sizeBigThan || degree > 0) {
            Luban.get(ctx)
                .load(File(checkifNeedRoateImage(orignalPath)))
                .putGear(zipType)
                .setCompressListener(object : Luban.OnCompressListener {
                    override fun onStart() {}

                    override fun onSuccess(file: File) {
                        logDeug("图片压缩", "图片压缩后大小" + getDebugString(file.absolutePath))
                        listener?.invoke(file.absolutePath)
                    }

                    override fun onError(e: Throwable) {
                        listener?.invoke(zipPictureToXX_XX(ctx, orignalPath))
                    }
                }).launch()
        } else {
            logDeug("图片压缩", "图片不需要压缩，直接上传")
            listener?.invoke(orignalPath)
        }
    }

    /**
     * @param path
     * @return
     */
    private fun checkifNeedRoateImage(path: String): String {
        val degree = readPictureDegree(path)
        if (degree == 0)
            return path

        logDeug("图片压缩", "图片需要旋转$degree")

        var bm: Bitmap? = null
        try {
            bm = rotaingImageView(degree, BitmapFactory.decodeFile(path))
            val fos = FileOutputStream(File(path))
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
        } finally {
            bm?.recycle()
            logDeug("图片压缩", "图片旋转后大小" + getDebugString(path))
        }

        return path
    }

    /**
     * @param TAG
     * @param log
     */
    private fun logDeug(TAG: String, log: String) {
        if (Constant.Debug) {
            Log.d("文件上传$TAG", log)
        }
    }

    /**
     * 使用Bitmap加Matrix来缩放
     */
    fun resizeImage(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        return Bitmap.createBitmap(
            bitmap, 0, 0, width,
            height, matrix, true
        )
    }
}

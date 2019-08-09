package com.soli.libcommon.util

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal

/**
 * @author Soli
 * @Time 18-5-17 下午2:26
 */
object FileUtil {

    private fun isExternalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED, ignoreCase = true)
    }

    /**
     * 获取目录
     *
     * @param context
     * @return
     */
    fun getRootDir(context: Context, isInAndroidDataFile: Boolean): File {

        var targetDir: File? = null

        try {
            if (isExternalMemoryAvailable()) {
                targetDir = if (isInAndroidDataFile) File(context.externalCacheDir!!.absolutePath + "/frame") else File(
                    Environment.getExternalStorageDirectory(),
                    "frame"
                )

                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }
            }
        } catch (e: Exception) {
        }

        if (targetDir == null || !targetDir.exists()) {
            targetDir = File(context.cacheDir.absolutePath + "/frame")
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
        }

        return targetDir
    }

    /**
     * 上传图片临时存放文件的位置
     *
     * @param context
     * @return
     */
    fun getPicUploadTempPath(context: Context, path: String): String {
        return File(getDir(context, "upload").absolutePath, getFileName("upload_", path)).absolutePath
    }

    /**
     *
     * @param prex 前缀
     * @param url 地址
     * @return
     */
    private fun getFileName(prex: String, url: String?): String {
        try {
            return "$prex${Utils.MD5(url!!)}.${getFileExtension(url)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "$prex${Utils.MD5(url ?: "dksldksldklskdlskdie2w0392039")}"
    }

    /**
     * 获取目录
     *
     * @param context
     * @param name
     * @return
     */
    fun getDir(context: Context, name: String): File {
        val file = File(getRootDir(context, true), name)
        if (!file.exists()) {
            file.mkdirs()
        }

        return file
    }

    /**
     * @param context
     * @param name
     * @param isInAndroidDataFile
     * @return
     */
    fun getDir(context: Context, name: String, isInAndroidDataFile: Boolean): File {
        val file = File(getRootDir(context, isInAndroidDataFile), name)
        if (!file.exists()) {
            file.mkdirs()
        }

        return file
    }

    /**
     * 获取文件
     *
     * @param context
     * @param dir
     * @param fileName
     * @return
     */
    fun getFile(context: Context, dir: String, fileName: String): File {
        return File(getDir(context, dir), fileName)
    }

    /**
     * 获取文件
     *
     * @param context
     * @param dir
     * @param fileName
     * @return
     */
    fun getFile(context: Context, dir: String, fileName: String, isInData: Boolean): File {
        return File(getDir(context, dir, isInData), fileName)
    }

    /**
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "0KB"
        }

        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    /**
     * @param b
     * @param ret
     * @return
     */
    fun getFileFromBytes(b: ByteArray?, ret: File): File {

        if (b == null) return ret

        var stream: BufferedOutputStream? = null
        try {
            val fstream = FileOutputStream(ret)
            stream = BufferedOutputStream(fstream)
            stream.write(b)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return ret
    }


    /**
     * 返回小写的后缀名
     * @param url
     * @return
     */
    private fun getSuffer(murl: String?): String {
        try {
            if (TextUtils.isEmpty(murl)) return ""

            var url = murl!!
            //网页过来的图片
            var index = url.indexOf("@!")
            if (index != -1) {
                url = url.substring(0, index)
            }

            //DMH xcode 链接 保存的文件名去掉后面的xcode
            index = url.indexOf("?xcode")
            if (index != -1) {
                url = url.substring(0, index)
            }

            index = url.indexOf("?")
            if (index != -1) {
                url = url.substring(0, index)
            }

            index = url.lastIndexOf(".")

            return (if (index != -1) url.substring(index + 1) else "").toLowerCase()
        } catch (e: Exception) {
        }

        return ""
    }

    /**
     * 获取文件后缀
     * */
    fun getFileExtension(url: String?): String {

        if (TextUtils.isEmpty(url)) return ""

        val temp = MimeTypeMap.getFileExtensionFromUrl(url)
        var extension = if (!TextUtils.isEmpty(temp)) temp.toLowerCase() else ""

        if (TextUtils.isEmpty(extension))
            extension = getSuffer(url)//从url后缀来识别

        if (TextUtils.isEmpty(extension))
            return ""

        if (!url!!.startsWith("http") && isImageFile(extension)) {
            //如果是本地图片，在从数据数据流中获取类型，进一步确认
            try {
                if (File(url).exists()) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(url, options)
                    val mimeType = options.outMimeType
                    MLog.d("mimeType", "后缀:->$extension  二进制：$mimeType")
                    if (mimeType.contains("image/"))
                        extension = mimeType.replace("image/", "").toLowerCase()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return extension
    }

    /**
     * 是否是音频文件
     *
     * @param filename
     * @return
     */
    fun isAudioFile(fileExt: String? = null, fullPath: String? = null): Boolean {

        var extension = fileExt

        if (!TextUtils.isEmpty(fullPath))
            extension = getFileExtension(fullPath)

        if (TextUtils.isEmpty(extension)) return false

        return arrayOf("mp3", "aac", "flac", "amr", "wav", "m4a", "ogg").indexOf(extension?.toLowerCase() ?: "") != -1
    }

    /**
     *
     */
    fun isVideoFile(fileExt: String? = null, fullPath: String? = null): Boolean {

        var extension = fileExt

        if (!TextUtils.isEmpty(fullPath))
            extension = getFileExtension(fullPath)

        if (TextUtils.isEmpty(extension)) return false

        return arrayOf("mp4", "m3u8", "3gp", "avi", "rm", "rmvb", "mkv", "mov", "m4v").indexOf(
            extension?.toLowerCase() ?: ""
        ) != -1
    }

    /**
     *
     */
    fun isImageFile(fileExt: String? = null, fullPath: String? = null): Boolean {

        var extension = fileExt

        if (!TextUtils.isEmpty(fullPath))
            extension = getFileExtension(fullPath)

        if (TextUtils.isEmpty(extension)) return false

        return arrayOf("jpg", "jpeg", "png", "webp", "apng", "gif", "bmp").indexOf(extension?.toLowerCase() ?: "") != -1
    }


    /**
     * 文件上传业务逻辑
     */
    fun getFileUploadType(fileExt: String): Int {
        return when {
            fileExt == "gif" -> 4
            isImageFile(fileExt) -> 3
            isVideoFile(fileExt) -> 2
            isAudioFile(fileExt) -> 1
            else -> 5
        }
    }

    /**
     * 获取视频文件的，视频的宽和高
     */
    fun getVideoFileWidthAndHeight(videoFile: String): Array<String> {
        val retr = MediaMetadataRetriever()
        retr.setDataSource(videoFile)
        return arrayOf(
            retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
            retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        )
    }

}
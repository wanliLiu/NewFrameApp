package com.soli.libcommon.util

import android.content.Context
import android.os.Environment
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
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
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
     * 是否是音频文件
     *
     * @param filename
     * @return
     */
    private fun isVolumeFile(fileExt: String): Boolean {
        val extensions = arrayOf("mp3", "amr", "wav", "aac", "m4a", "ogg")
        for (i in extensions.indices) {
            if (fileExt == extensions[i]) {
                return true
            }
        }
        return false
    }

    private fun isVideoFile(fileExt: String): Boolean {
        val extensions = arrayOf("mp4", "3gp", "avi", "rm", "rmvb", "mkv", "mov", "m4v")
        for (i in extensions.indices) {
            if (fileExt == extensions[i]) {
                return true
            }
        }
        return false
    }

    private fun isImageFile(fileExt: String): Boolean {
        val extensions = arrayOf("jpg", "jpeg", "png", "bmp")//"gif
        for (i in extensions.indices) {
            if (fileExt == extensions[i]) {
                return true
            }
        }
        return false
    }


    fun getFileUploadType(fileExt: String): Int {
        return when {
            fileExt == "gif" -> 4
            isImageFile(fileExt) -> 3
            isVideoFile(fileExt) -> 2
            isVolumeFile(fileExt) -> 1
            else -> 5
        }
    }

}
package com.soli.libcommon.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 * Android Q版本应用兼容性适配指导
 * https://blog.csdn.net/irizhao/article/details/94121551
 *
 * @author Soli
 * @Time 18-5-17 下午2:26
 */
object FileUtil {

    const val UserMediaPath = "DCIM/Camera"

    //是否是Android Q以上
    val isAndroidQorAbove: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val isExternalMemoryAvailable: Boolean
        get() = Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED,
            ignoreCase = true
        )

    /**
     * 获取需要下载到本地，并且用户需要看到的目录
     */
    fun getUserCanSeeDir(ctx: Context): File {

        val dir = if (isExternalMemoryAvailable) File(
            Environment.getExternalStorageDirectory().absolutePath,
            UserMediaPath
        ) else
            getRootDir(ctx, false)

        if (!dir.exists())
            dir.mkdirs()

        return dir
    }


    /**
     * 获取目录
     *
     * @param context
     * @return
     */
    private fun getRootDir(context: Context, isInAndroidDataFile: Boolean): File {
        var targetDir: File? = null

        try {
            targetDir = if (isInAndroidDataFile)
                context.cacheDir
            else {
                context.externalCacheDir
            }

            if (!targetDir!!.exists()) {
                targetDir.mkdirs()
            }
        } catch (e: Exception) {
        }

        if (targetDir == null || !targetDir.exists()) {
            targetDir = context.cacheDir
            if (!targetDir!!.exists()) {
                targetDir.mkdirs()
            }
        }

        return targetDir
    }

    /**
     * 删除文件
     */
    fun delete(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        val file = File(filePath!!)
        return if (file.isFile && file.exists()) {
            file.delete()
        } else false
    }

    /**
     * 上传图片临时存放文件的位置
     *
     * @param context
     * @return
     */
    fun getPicUploadTempPath(context: Context, path: String): String {
        return File(
            getDir(context, "upload").absolutePath,
            getFileName("upload_", path)
        ).absolutePath
    }

    /**
     *
     * @param prex 前缀
     * @param url 地址
     * @return
     */
    fun getFileName(prex: String, url: String?): String {
        try {
            return "$prex${Utils.MD5(url!!)}.${getFileExtension(url)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "$prex${Utils.MD5(url ?: "dksldksldklskdlskdie2w0392039")}"
    }

    /**
     * 根据时间来设置照片的名字
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getPictureName(head: String): String {
        val format = SimpleDateFormat("yyyyMMdd_hhmmss")
        val date = Date(System.currentTimeMillis())

        return head + format.format(date) + ".jpeg"
    }

    /**
     * @param context
     * @param name
     * @param isInAndroidDataFile
     * @return
     */
    fun getDir(context: Context, name: String, isInAndroidDataFile: Boolean = true): File {
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
    fun getFile(context: Context, dir: String, fileName: String, isInData: Boolean = true): File {
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
     *
     */
    fun getFileFromInputStream(inputStrem: InputStream, file: File): File {
        val bis = BufferedInputStream(inputStrem)
        val buffer = ByteArray(8192)
        val stream = FileOutputStream(file)

        var bytesRead = bis.read(buffer)
        while (bytesRead != -1) {
            //不是暂停命令，不是取消命令，数据没写入完，就执行下面代码
            stream.write(buffer, 0, bytesRead)
            //获取当前进度值
            bytesRead = bis.read(buffer)
        }
        stream.close()
        bis.close()

        return file
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

            return (if (index != -1) url.substring(index + 1) else "").lowercase()
        } catch (e: Exception) {
        }

        return ""
    }

    /**
     * 获取文件后缀
     * */
    fun getFileExtension(str: String?): String {
        var extension = ""
        val url: String = str ?: ""

        if (TextUtils.isEmpty(url)) return ""

        if (!url.startsWith("http")) {
            //如果是本地图片，在从数据数据流中获取类型，进一步确认
            try {
                if (File(url).exists()) {
                    val options = BitmapFactory.Options()

                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(url, options)
                    val mimeType = options.outMimeType
                    Log.d("mimeType", "  二进制：$mimeType")
                    if (mimeType.contains("image/"))
                        extension = mimeType.replace("image/", "").lowercase()
                }
            } catch (e: Exception) {
            }
        }

        if (!TextUtils.isEmpty(extension)) return extension

        val temp = MimeTypeMap.getFileExtensionFromUrl(url)
        extension = if (!TextUtils.isEmpty(temp))
            temp.lowercase()
        else
            ""

        if (TextUtils.isEmpty(extension))
            extension = getSuffer(url)//从url后缀来识别

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

        return arrayOf(
            "mp3",
            "aac",
            "flac",
            "amr",
            "wav",
            "m4a",
            "ogg"
        ).indexOf(extension?.lowercase() ?: "") != -1
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
            extension?.lowercase() ?: ""
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

        return arrayOf(
            "jpg",
            "jpeg",
            "png",
            "webp",
            "apng",
            "gif",
            "bmp"
        ).indexOf(extension?.lowercase() ?: "") != -1
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
            retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "",
            retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: ""
        )
    }

    /**
     * 获取网络下载图片地址保存目录,自动根据文件类型保存到相应的文件夹里
     * @param context
     * @param url
     * @param isIn    保存的位置
     * @return
     */
    fun getDownLoadFilePath(context: Context, url: String, isIn: Boolean = false): File {

        var dirName = "download"
        try {
            //网络文件
            if (url.startsWith("http")) {
                val extension = getFileExtension(url)
                dirName += when {
                    isImageFile(extension) -> "/picture"
                    isAudioFile(extension) -> "/audio"
                    isVideoFile(extension) -> "/video"
                    else -> ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return getFile(context, dirName, getFileName("frame_", url), isIn)
    }


    /**
     * 加入下载的文件到系统媒体数据库
     */
    fun scanMediaForFile(ctx: Context, filePath: String?) {
        if (TextUtils.isEmpty(filePath)) return

        MediaScannerConnection.scanFile(
            ctx,
            arrayOf(filePath),
            null
        ) { path, uri -> Log.d("scanMedia", "${path ?: ""} -->${uri?.toString() ?: ""}") }
    }


    /**
     * Android 10上，存储文件到公共目录，picture 和video都放到DCIM/Camera  音频放到Musics目录下
     * 思路就是，app 先下载文件到自己的沙盒地方，然后从沙盒的地方通过ConternResolver拷贝到公共目录
     *
     * 类似的需求是，用户下载图片或者拍摄照片或者下载视频、音频
     */
    @TargetApi(Build.VERSION_CODES.Q)
    fun storeFileInPublicAtTargetQ(ctx: Context, file: File?) {
        file ?: return
        if (!file.exists()) return

        var external: Uri? = null

        try {
            Thread {
                val values = when {
                    isVideoFile(fullPath = file.absolutePath) -> ContentValues().apply {
                        put(MediaStore.Video.Media.DESCRIPTION, "Create from" + ctx.packageName)
                        put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
                        put(MediaStore.Video.Media.RELATIVE_PATH, UserMediaPath)
                        external = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    isImageFile(fullPath = file.absolutePath) -> ContentValues().apply {
                        put(MediaStore.Images.Media.DESCRIPTION, "Create from" + ctx.packageName)
                        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                        put(MediaStore.Images.Media.RELATIVE_PATH, UserMediaPath)
                        external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    isAudioFile(fullPath = file.absolutePath) -> ContentValues().apply {
                        put(MediaStore.Audio.Media.DISPLAY_NAME, file.name)
                        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                        external = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> null
                }

                values ?: return@Thread
                external ?: return@Thread

                val resolver = ctx.contentResolver
                //相同的文件如果已经insert的话，这里这个inserUri会返回null
                val inserUri = resolver.insert(external!!, values)

                var os: OutputStream? = null
                try {
                    if (inserUri != null)
                        os = resolver.openOutputStream(inserUri)

                    os ?: return@Thread

                    val inputStream = FileInputStream(file)
                    val buffer = ByteArray(1024)
                    var byteRead = inputStream.read(buffer)
                    while (byteRead != -1) {
                        os.write(buffer, 0, byteRead)
                        byteRead = inputStream.read(buffer)
                    }
                    os.close()
                    inputStream.close()

                    //最后删除源文件
                    file.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    os?.close()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 从公共区域拷贝文件到自己的沙盒，用于后续的文件压缩，处理
     * @param source 源文件的Uri
     * @param destfile 需要拷贝的目标文件地址
     */
    fun copyFileFromPublicToPrivateAtTargetQ(ctx: Context, source: Uri, destfile: File): File? {

        try {
            val fileIn = ctx.contentResolver.openInputStream(source)
            fileIn ?: return null

            val fileOut = FileOutputStream(destfile)
            val buffer = ByteArray(1024)
            var byteRead = fileIn.read(buffer)
            while (byteRead != -1) {
                fileOut.write(buffer, 0, byteRead)
                byteRead = fileIn.read(buffer)
            }
            fileIn.close()
            fileOut.close()

            return destfile
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
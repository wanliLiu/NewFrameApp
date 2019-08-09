package com.soli.libcommon.util

import java.io.File
import java.math.BigDecimal

/**
 * Created by Soli on 2016/7/15.
 */
object FileSizeUtil {
    val SIZETYPE_B = 1// 获取文件大小单位为B的double值
    val SIZETYPE_KB = 2// 获取文件大小单位为KB的double值
    val SIZETYPE_MB = 3// 获取文件大小单位为MB的double值
    val SIZETYPE_GB = 4// 获取文件大小单位为GB的double值

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    fun getFileOrFilesSize(filePath: String, sizeType: Int): Double {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return FormetFileSize(blockSize, sizeType)
    }

    /**
     * 获取文件大小 kb单位
     *
     * @param filePath
     * @return
     */
    fun getFileSize(filePath: String): String {
        val size = getFileOrFilesSize(filePath, SIZETYPE_KB)
        return BigDecimal(size).setScale(0, BigDecimal.ROUND_HALF_UP).toString()
    }


    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    fun getAutoFileOrFilesSize(filePath: String): String {
        val file = File(filePath)
        var blockSize: Long = 0
        try {
            blockSize = if (file.isDirectory) {
                getFileSizes(file)
            } else {
                getFileSize(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return FormetFileSize(blockSize)
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFileSizes(f: File): Long {
        var size: Long = 0
        val flist = f.listFiles()
        for (i in flist.indices) {
            size = if (flist[i].isDirectory) {
                size + getFileSizes(flist[i])
            } else {
                size + getFileSize(flist[i])
            }
        }
        return size
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            size = file.length()
        }
        return size
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    fun FormetFileSize(fileS: Long): String {
        if (fileS == 0L) {
            return "0B"
        }
        return when {
            fileS < 1024 -> BigDecimal(fileS.toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "B"
            fileS < 1048576 -> BigDecimal(fileS.toDouble() / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "KB"
            fileS < 1073741824 -> BigDecimal(fileS.toDouble() / 1048576).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "MB"
            else -> BigDecimal(fileS.toDouble() / 1073741824).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "GB"
        }
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     *
     * 不用这个格式化，这个和国家有关，有些国家直接是，
     * DecimalFormat df = new DecimalFormat("#.00");
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private fun FormetFileSize(fileS: Long, sizeType: Int): Double {
        var size = 0.0
        when (sizeType) {
            SIZETYPE_B -> size = fileS.toDouble()
            SIZETYPE_KB -> size = fileS.toDouble() / 1024
            SIZETYPE_MB -> size = fileS.toDouble() / 1048576
            SIZETYPE_GB -> size = fileS.toDouble() / 1073741824
            else -> {
            }
        }

        return BigDecimal(size).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}

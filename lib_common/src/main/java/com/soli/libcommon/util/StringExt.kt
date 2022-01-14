package com.soli.libcommon.util

import okio.ByteString.Companion.readByteString
import java.io.File
import java.util.regex.Pattern

/**
 * 根据正则截取子串
 */
fun String.substring(regex: String): String? {
    val matcher = Pattern.compile(regex).matcher(this)
    if (matcher.find()) {
        return matcher.group(0);
    }
    return null
}

fun String.mkdir(flags: Int) {
    val file = File(this)
    if (!file.exists()) {
        file.mkdir()
    }
    Runtime.getRuntime().exec("chmod $flags $this")
}

/**
 * 执行命令
 * @param successCallback 成功回调
 * @param failure 失败回调
 */
fun String.exec(
    successCallback: ((data: ByteArray) -> Unit)? = null,
    failure: ((msg: Exception) -> Unit)? = null
) {
    try {
        MLog.d("cmd", this)
        val process = Runtime.getRuntime().exec(this)
        if (process.waitFor() == 0) {
            val inputStream = process.inputStream
            val data = ByteArray(inputStream.available())
            inputStream.read(data)
            successCallback?.invoke(data)
        } else {
            val dataSize = process.errorStream.available()
            val errStr = process.errorStream.readByteString(dataSize).utf8()
            failure?.invoke(Exception(errStr))
        }
        process.destroy()
    } catch (e: Exception) {
        failure?.invoke(e)
    }
}

/**
 * 根据正则匹配内容
 */
fun String.find(regex: String): String? {
    return Pattern.compile(regex)
        .matcher(this)
        .let {
            if (it.find())
                it.group(1)
            else null
        }
}

fun String.groups(regex: String): List<String>? {
    return Pattern.compile(regex)
        .matcher(this)
        .let {
            var groups: ArrayList<String>? = null
            while (it.find()) {
                if (groups == null) groups = arrayListOf()
                it.group(1)?.apply {
                    groups.add(this)
                }
            }
            groups
        }
}
package com.soli.libcommon.net.upload

/**
 *
 * @author Soli
 * @Time 2018/12/6 16:54
 */
data class FileUploadInfo(
    val originPath: String,//文件原地址
    var urlPath: String,//上传成功后的地址
    val width: Int,//图片高度
    val height: Int//图片宽度
) {
    init {
        urlPath = "$urlPath?_width=$width&_height=$height"
    }
}
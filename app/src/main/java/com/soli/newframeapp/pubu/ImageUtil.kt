package com.soli.newframeapp.pubu

/**
 *
 * @author Soli
 * @Time 2018/12/7 16:27
 */
object ImageUtil {


    data class ImageInfo(val url: String, val width: Int, val height: Int) {
        fun isDataOkay() = url.indexOf("?_") == -1 && width > 0 && height > 0
    }

    /**
     * 服务器返回的图片地址，后面跟的是我们的?_width=100&_height=200  这个函数是拿出这个信息
     */
    fun getImageInfo(path: String): ImageInfo {
        try {
            val index = path.indexOf("?_")
            if (index == -1)
                return ImageInfo(path, 0, 0)

            val last = path.substring(index + 1)
            if (!last.contains("&_height"))
                return ImageInfo(path, 0, 0)

            val split = last.split("&")
            if (split.size != 2)
                return ImageInfo(path, 0, 0)

            var width = 0
            var height = 0
            for (path in split) {
                if (path.contains("width")) {
                    width = path.split("=")[1].toFloat().toInt()
                } else if (path.contains("height"))
                    height = path.split("=")[1].toFloat().toInt()
            }
            return ImageInfo(path.substring(0, index), width, height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ImageInfo(path, 0, 0)
    }

    /**
     * 网络请求的都走这个
     */
    fun getRequestUrl(path: String, width: Int, height: Int = 0): String {
//        return path

        if (!path.contains("joker.taihe.com"))
            return path

        val info = getImageInfo(path)

        if (width == 0)//|| height == 0
            return info.url

        return "${info.url}@w_$width"
//        return "${info.url}@w_$width,h_$height"
    }

    /**
     *
     */
    fun addSomethingBeforeUpload(path: String) = "#taihe_xuezhiqian_baseUrl#$path"

}
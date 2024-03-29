package com.soli.newframeapp.model

/**
 *
 * @author Soli
 * @Time 12/16/20 10:33 AM
 */

data class Story(
    /**
     * 新闻标题
     */
    val title: String,
    /**
     * 供 Google Analytics 使用
     */
    val ga_prefix: String,
    /**
     * 图像地址（官方 API 使用数组形式。目前暂未有使用多张图片的情形出现，曾见无 images 属性的情况，请在使用中注意 ）
     **/
    val images: Array<String>,
    /**
     * 消息是否包含多张图片（仅出现在包含多图的新闻中）
     */
    var multipic: String,
    var type: String,
    /**
     * url 与 share_url 中最后的数字（应为内容的 id）
     */

    var id: String
)

data class StoryList(
    val date : String,
    val stories : List<Story>
)
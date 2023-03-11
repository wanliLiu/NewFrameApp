package com.soli.newframeapp.pubu

import android.content.Context
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.libcommon.util.ImageLoader
import com.soli.libcommon.util.Utils
import com.soli.newframeapp.databinding.ItemJokerPhotoBinding

/**
 *
 *
 *时间：2018/12/5
 *作者：CDY
 */
class PhotoAdapter(context: Context) : BaseRecycleAdapter<String, ItemJokerPhotoBinding>(context) {

    private val itemWidth by lazy { Utils.getScreenWidthPixels(context) / 2 }

    override fun onBindView(
        binding: ItemJokerPhotoBinding,
        itemType: Int,
        originalPosition: Int,
        realPosition: Int,
        payloads: List<Any>
    ) {
        val bean = list[realPosition]

        val imageInfo = ImageUtil.getImageInfo(bean)

        val rat = if (imageInfo.isDataOkay())
            imageInfo.height * 1.0 / imageInfo.width * 1.0
        else {
            0.5
        }

        val params = binding.photo.layoutParams
        params.height = (itemWidth * rat).toInt()
        binding.photo.layoutParams = params

        ImageLoader.loadImage(binding.photo, bean, itemWidth, itemWidth)
    }

}
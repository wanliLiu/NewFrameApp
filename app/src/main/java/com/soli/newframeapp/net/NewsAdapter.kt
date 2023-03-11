package com.soli.newframeapp.net

import android.content.Context
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.libcommon.util.ImageLoader
import com.soli.newframeapp.databinding.ItemNewsListBinding
import com.soli.newframeapp.model.Story

/*
 * @author soli
 * @Time 2018/5/26 21:33
 */
class NewsAdapter(ctx: Context) : BaseRecycleAdapter<Story, ItemNewsListBinding>(ctx) {

    override fun onBindView(
        binding: ItemNewsListBinding,
        itemType: Int,
        originalPosition: Int,
        realPosition: Int,
        payloads: List<Any>
    ) {
        val data = getItemData(realPosition) ?: return

        if (data.images.isNotEmpty())
            ImageLoader.loadImage(binding.newsImage, data.images[0])
        binding.newsTitle.text = data.title
    }
}
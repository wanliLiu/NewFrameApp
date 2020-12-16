package com.soli.newframeapp.net

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.libcommon.util.ImageLoader
import com.soli.newframeapp.R
import com.soli.newframeapp.model.Story

/*
 * @author soli
 * @Time 2018/5/26 21:33
 */
class NewsAdapter(ctx: Context) : BaseRecycleAdapter<Story>(ctx) {

    override fun onCreateView(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(ctx).inflate(R.layout.item_news_list, parent, false)
        )
    }

    override fun onBindView(
        mholder: RecyclerView.ViewHolder?,
        itemType: Int,
        originalPosition: Int,
        realPosition: Int,
        payloads: List<Any>
    ) {
        val data = getItemData(realPosition) ?: return

        (mholder as? NewsViewHolder)?.apply {
            if (data.images.isNotEmpty())
                ImageLoader.loadImage(news_image, data.images[0])
            news_title.text = data.title
        }
    }

    private class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val news_image: SimpleDraweeView = view.findViewById(R.id.news_image)
        val news_title: TextView = view.findViewById(R.id.news_title)
    }
}
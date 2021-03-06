package com.soli.newframeapp.net

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    override fun onCreateViewHolder_impl(viewGroup: ViewGroup?, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return NewsViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_news_list, viewGroup, false))
    }

    override fun onBindViewHolder_impl(
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?,
        itemType: Int,
        original_position: Int,
        real_position: Int
    ) {
        val data = getItemData(real_position)
        val holder = viewHolder as NewsViewHolder
        if (data.images.isNotEmpty())
            ImageLoader.loadImage(holder.news_image, data.images[0])
        holder.news_title.text = data.title
    }


    private class NewsViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val news_image: SimpleDraweeView = view.findViewById(R.id.news_image)
        val news_title: TextView = view.findViewById(R.id.news_title)
    }
}
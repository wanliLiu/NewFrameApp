package com.soli.newframeapp.net

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.soli.lib_common.base.BaseRecycleAdapter
import com.soli.lib_common.util.ImageLoader
import com.soli.newframeapp.R
import com.soli.newframeapp.model.Story

/*
 * @author soli
 * @Time 2018/5/26 21:33
 */
class NewsAdapter(ctx: Context) : BaseRecycleAdapter<Story>(ctx) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NewsViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_news_list, parent, false))
    }

    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(mholder, position)
        val data = getItemData(position)
        val holder = mholder as NewsViewHolder
        if (data.images.isNotEmpty())
            ImageLoader.loadImage(holder.news_image, data.images[0])
        holder.news_title.text = data.title
    }


    private class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val news_image: SimpleDraweeView = view.findViewById(R.id.news_image)
        val news_title: TextView = view.findViewById(R.id.news_title)
    }
}
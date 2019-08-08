package com.soli.newframeapp.pubu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.libcommon.util.ImageLoader
import com.soli.libcommon.util.Utils
import com.soli.newframeapp.R

/**
 *
 *
 *时间：2018/12/5
 *作者：CDY
 */
class PhotoAdapter(context: Context) : BaseRecycleAdapter<String>(context) {


    private val itemWidth by lazy { Utils.getScreenWidthPixels(context) / 2 }

    override fun onCreateViewHolder_impl(viewGroup: ViewGroup?, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder =
        ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_joker_photo, viewGroup, false))


    override fun onBindViewHolder_impl(
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?,
        itemType: Int,
        original_position: Int,
        real_position: Int
    ) {

        val bean = list[real_position] ?: return
        val holder = viewHolder as ViewHolder


        val imageInfo = ImageUtil.getImageInfo(bean)

        val rat = if (imageInfo.isDataOkay())
            imageInfo.height * 1.0 / imageInfo.width * 1.0
        else {
            0.5
        }

        val params = holder.f_image_photo.layoutParams
        params.height = (itemWidth * rat).toInt()
        holder.f_image_photo.layoutParams = params

        ImageLoader.loadImage(holder.f_image_photo, bean, itemWidth, itemWidth)
    }

    private class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val f_image_photo = view as SimpleDraweeView
    }

}
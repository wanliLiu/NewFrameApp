package com.soli.newframeapp.pubu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreateView(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
      return   ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_joker_photo, parent, false))
    }


    override fun onBindView(
        mholder: RecyclerView.ViewHolder?,
        itemType: Int,
        originalPosition: Int,
        realPosition: Int,
        payloads: List<Any>
    ) {
        val bean = list[realPosition] ?: return
        val holder = mholder as? ViewHolder

        holder ?: return

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

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val f_image_photo = view as SimpleDraweeView
    }

}
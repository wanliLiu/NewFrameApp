package com.soli.newframeapp.autowrap

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.soli.libcommon.view.flexboxlayout.AutoWrapAdapter
import com.soli.newframeapp.databinding.ItemAutoWarpBinding

/**
 *
 * @author Soli
 * @Time 2018/11/27 14:56
 */
class TestAutoWrapAdapter(ctx: Context, list: MutableList<String>? = null) :
    AutoWrapAdapter<String>(ctx, list) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = ItemAutoWarpBinding.inflate(mInflater)
        view.btn.text = getItem(position)
        return view.root
    }
}
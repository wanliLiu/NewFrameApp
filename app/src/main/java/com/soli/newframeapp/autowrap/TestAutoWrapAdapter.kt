package com.soli.newframeapp.autowrap

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.soli.libCommon.view.flexboxlayout.AutoWrapAdapter
import com.soli.newframeapp.R

/**
 *
 * @author Soli
 * @Time 2018/11/27 14:56
 */
class TestAutoWrapAdapter(ctx: Context, list: MutableList<String>? = null) : AutoWrapAdapter<String>(ctx, list) {

    constructor(ctx: Context) : this(ctx, null)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = mInflater.inflate(R.layout.item_auto_warp, null) as Button
        view.text = getItem(position)
        return view
    }
}
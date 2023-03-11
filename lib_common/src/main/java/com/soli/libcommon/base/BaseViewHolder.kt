package com.soli.libcommon.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 *
 * @author soli
 * @Time 2023/3/11 16:47
 */
class BaseViewHolder<Binding : ViewBinding>(bind: Binding) : RecyclerView.ViewHolder(bind.root) {
    val binding = bind
}
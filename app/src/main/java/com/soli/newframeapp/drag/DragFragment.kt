package com.soli.newframeapp.drag

import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.newframeapp.R

/**
 *
 * <p>
 * Created by sofia on 2021/5/20.
 */
class DragFragment : BaseToolbarFragment() {
    override fun getContentView() = R.layout.fragment_drag

    override fun initView() {
        setTitle("ViewDragHelper")
    }
    override fun initListener() = Unit
    override fun initData() = Unit
}
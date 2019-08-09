package com.soli.newframeapp.bottomsheet

import android.os.Bundle
import android.view.View
import com.soli.libcommon.bottomsheet.BaseSheetFragment
import com.soli.newframeapp.R


/**
 *
 * @author Soli
 * @Time 2018/11/12 13:47
 */
class BottomSheetFragment : BaseSheetFragment() {

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BottomSheetFragment() }
    }

    override fun getContentView() = R.layout.view_bottomsheet

    override fun initView(view: View, savedInstanceState: Bundle?) {

//        topOffsetDefault()
    }

    override fun initListener() {
    }

    override fun initData() {
    }

}
package com.soli.newframeapp.fragment

import androidx.viewbinding.ViewBinding
import com.soli.libcommon.base.BaseFragment

/*
 * @author soli
 * @Time 2020/4/21 21:57
 */

abstract class BaseTabHomeFragment<Binding : ViewBinding> : BaseFragment<Binding>(),
    OnDoubleClickListener {

    override fun onDoubleClickHappen() {
    }
}
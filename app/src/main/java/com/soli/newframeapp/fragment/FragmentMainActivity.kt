package com.soli.newframeapp.fragment

import androidx.core.os.bundleOf
import com.soli.libcommon.base.BaseFragmentActivity
import com.soli.libcommon.util.openFragment

/**
 *
 * @author Soli
 * @Time 2020/4/20 14:27
 */
class FragmentMainActivity : BaseFragmentActivity() {
    override fun initView() {

    }

    override fun initListener() {

    }

    override fun initData() {
        openFragment<TestFragment>(bundleOf("title" to "第一个", "content" to "我是第一个内容"),showAnimation = false)
    }
}
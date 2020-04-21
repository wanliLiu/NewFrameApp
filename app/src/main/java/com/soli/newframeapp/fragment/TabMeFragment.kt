package com.soli.newframeapp.fragment

import androidx.core.os.bundleOf
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.fragment_me_view.*

/**
 *
 * @author Soli
 * @Time 2020/4/21 10:13
 */
class TabMeFragment : BaseTabHomeFragment() {
    override fun getContentView() = R.layout.fragment_me_view

    override fun initView() {
    }

    override fun initListener() {
        displayText.setOnClickListener {
            openFragment<TestFragment>(
                bundleOf("title" to "第一个", "content" to "我是第一个内容")
            )
        }
    }

    override fun initData() {
    }

}
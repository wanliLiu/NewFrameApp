package com.soli.newframeapp.fragment

import androidx.core.os.bundleOf
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.fragment_home_view.*

/**
 *
 * @author Soli
 * @Time 2020/4/21 10:13
 */
class TabHomeFragment : BaseAnimationFragment() ,OnDoubleClickListener{
    override fun getContentView() = R.layout.fragment_home_view

    override fun initView() {
    }

    override fun initListener() {
    }

    override fun initData() {

        displayText.setOnClickListener {
            openFragment<TestFragment>(
                bundleOf("title" to "第一个", "content" to "我是第一个内容")
            )
        }
    }

    override fun onDoubleClickHappen() {
    }
}
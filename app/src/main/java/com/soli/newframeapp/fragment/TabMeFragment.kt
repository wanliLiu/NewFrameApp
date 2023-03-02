package com.soli.newframeapp.fragment

import androidx.core.os.bundleOf
import com.soli.newframeapp.databinding.FragmentMeViewBinding
import com.soli.newframeapp.event.openFragment

/**
 *
 * @author Soli
 * @Time 2020/4/21 10:13
 */
class TabMeFragment : BaseTabHomeFragment<FragmentMeViewBinding>() {
    override fun initView() {
    }

    override fun initListener() {
        binding.displayText.setOnClickListener {
            openFragment(TestFragment().apply {
                arguments = bundleOf("title" to "第一个", "content" to "我是第一个内容")
            })
        }
    }

    override fun initData() {
    }

}
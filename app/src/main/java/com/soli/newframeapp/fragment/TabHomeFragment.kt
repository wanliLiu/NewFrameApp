package com.soli.newframeapp.fragment

import androidx.core.os.bundleOf
import com.soli.newframeapp.databinding.FragmentHomeViewBinding
import com.soli.newframeapp.event.openFragment

/**
 *
 * @author Soli
 * @Time 2020/4/21 10:13
 */
class TabHomeFragment : BaseTabHomeFragment<FragmentHomeViewBinding>() {
    override fun initView() {
    }

    override fun initListener() {
    }

    override fun initData() {

        binding.displayText.setOnClickListener {
           openFragment(TestFragment().apply {
               arguments = bundleOf("title" to "第一个", "content" to "我是第一个内容")
           })
        }
    }
}
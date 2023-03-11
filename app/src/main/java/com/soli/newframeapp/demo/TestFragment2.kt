package com.soli.newframeapp.demo

import com.soli.libcommon.base.BaseFragment
import com.soli.libcommon.base.BaseRecycleAdapter
import com.soli.newframeapp.databinding.FragmentTestBinding
import com.soli.newframeapp.databinding.ItemTestFragmentBinding

/**
 *
 * @author Soli
 * @Time 2020/7/24 10:08
 */
class TestFragment2 : BaseFragment<FragmentTestBinding>() {
    override fun initView() {
    }

    override fun initListener() {
    }

    override fun initData() {
        binding.artDetailList.adapter =
            object : BaseRecycleAdapter<String, ItemTestFragmentBinding>(ctx!!) {

                override fun getItemCount(): Int = 1000

                override fun onBindView(
                    binding: ItemTestFragmentBinding,
                    itemType: Int,
                    originalPosition: Int,
                    realPosition: Int,
                    payloads: List<Any>
                ) {
                    binding.testItem.text = "数据开始-->$realPosition"
                }

            }
    }


}
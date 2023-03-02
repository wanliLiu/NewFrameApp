package com.soli.newframeapp

import android.os.Bundle
import android.os.Handler
import com.soli.libcommon.base.BaseFragment
import com.soli.newframeapp.databinding.LayoutFramgentBinding

/**
 * @author Soli
 * @Time 18-5-16 下午2:01
 */
class TestFragment : BaseFragment<LayoutFramgentBinding>() {

    companion object {
        val inputStr: String = "str"

        /**
         *
         */
        fun getInstance(string: String = "默认参数"): TestFragment {
            val fragment = TestFragment()
            val bundle = Bundle()
            bundle.putString(inputStr, string)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        arguments?.apply {
            binding.desc.text = getString(inputStr, "没有数据传入")
        }
    }

    override fun initListener() {
        binding.tigger.setOnClickListener {
            if ("java" in binding.desc.text.toString()) {
                hasNoResult()
            } else
                initData()
        }
    }

    override fun initData() {
        showProgress()
        Handler().postDelayed({
            dismissProgress()
        }, 2000)
    }
}
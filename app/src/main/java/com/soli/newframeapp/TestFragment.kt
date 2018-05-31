package com.soli.newframeapp

import android.os.Bundle
import android.os.Handler
import com.soli.lib_common.base.BaseFragment
import kotlinx.android.synthetic.main.layout_framgent.*

/**
 * @author Soli
 * @Time 18-5-16 下午2:01
 */
class TestFragment : BaseFragment() {

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

    override fun getContentView(): Int {
        return R.layout.layout_framgent
    }

    override fun initView() {
        arguments?.apply {
            desc.text = getString(inputStr, "没有数据传入")
        }
    }

    override fun initListener() {
        tigger.setOnClickListener {
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
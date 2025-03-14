package com.soli.newframeapp.fragment

import android.graphics.Color
import androidx.core.os.bundleOf
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.newframeapp.databinding.TestFragmentViewBinding
import com.soli.newframeapp.event.openFragment
import java.util.*

/**
 *
 * @author Soli
 * @Time 2020/4/20 14:31
 */
class TestOneFragment : BaseToolbarFragment<TestFragmentViewBinding>() {

    companion object {
        var index = 0
    }

    override fun initView() {
        setTitle(arguments?.getString("title") ?: "未知标题")
        binding.backView.setBackgroundColor(Color.parseColor(getRandColor()))
        rootView.setToolbarBackgroudColor(Color.parseColor(getRandColor()))
    }

    override fun initListener() {

        binding.displayText.setOnClickListener {
            openFragment(TestFragment().apply {
                arguments = bundleOf(
                    "title" to "标题${index}",
                    "content" to "我是内容"
                )
            })
            index++
        }
    }

    private fun getRandColor(): String? {
        var R: String
        var G: String
        var B: String
        val random = Random()
        R = Integer.toHexString(random.nextInt(256)).uppercase()
        G = Integer.toHexString(random.nextInt(256)).uppercase()
        B = Integer.toHexString(random.nextInt(256)).uppercase()
        R = if (R.length == 1) "0$R" else R
        G = if (G.length == 1) "0$G" else G
        B = if (B.length == 1) "0$B" else B
        return "#$R$G$B"
    }

    override fun initData() {
        binding.displayText.text = arguments?.getString("title") ?: "未知标题"
    }
}
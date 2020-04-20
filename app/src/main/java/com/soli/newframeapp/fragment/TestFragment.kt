package com.soli.newframeapp.fragment

import android.graphics.Color
import androidx.core.os.bundleOf
import com.soli.libcommon.base.BaseFragmentActivity
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.libcommon.util.openFragment
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.test_fragment_view.*
import java.util.*

/**
 *
 * @author Soli
 * @Time 2020/4/20 14:31
 */
class TestFragment : BaseToolbarFragment() {

    override fun getContentView() = R.layout.test_fragment_view

    override fun initView() {
        setTitle(arguments?.getString("title") ?: "未知标题")
        backView.setBackgroundColor(Color.parseColor(getRandColor()))
        rootView.setToolbarBackgroudColor(Color.parseColor(getRandColor()))
    }

    override fun initListener() {

        displayText.setOnClickListener {
            if (ctx is BaseFragmentActivity)
                (ctx as BaseFragmentActivity).openFragment<TestFragment>(
                    bundleOf(
                        "title" to "标题${(1..100).random()}",
                        "content" to "我是内容"
                    )
                )
        }
    }

    private fun getRandColor(): String? {
        var R: String
        var G: String
        var B: String
        val random = Random()
        R = Integer.toHexString(random.nextInt(256)).toUpperCase()
        G = Integer.toHexString(random.nextInt(256)).toUpperCase()
        B = Integer.toHexString(random.nextInt(256)).toUpperCase()
        R = if (R.length == 1) "0$R" else R
        G = if (G.length == 1) "0$G" else G
        B = if (B.length == 1) "0$B" else B
        return "#$R$G$B"
    }

    override fun initData() {
        displayText.text = arguments?.getString("title") ?: "未知标题"
    }

    override fun onResume() {
        super.onResume()
    }
}
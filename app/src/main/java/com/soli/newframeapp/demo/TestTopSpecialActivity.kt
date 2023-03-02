package com.soli.newframeapp.demo

import com.soli.libcommon.base.BaseTopSpecialActivity
import com.soli.libcommon.util.*
import com.soli.newframeapp.databinding.ActivityForAnotherTopSpecialModelBinding

/**
 *
 * @author Soli
 * @Time 2018/11/15 11:17
 */
class TestTopSpecialActivity : BaseTopSpecialActivity<ActivityForAnotherTopSpecialModelBinding>() {

    override fun initView() {
        super.initView()
        title = "效果demo模块"
    }

    override fun initListener() {

        binding.appBarLayout.addOnOffsetChangedListener { layout, verticalOffset ->
            val scrollRangle = layout.totalScrollRange
            val alpha = Math.abs(verticalOffset) * 1.0 / scrollRangle * 1.0
            MLog.e("verticalOffset", alpha.toString())
            rootView.setToolbarBackgroudColor(
                StatusBarUtil.getColorWit1hAlpha1(
                    alpha, ctx.resources.getColor(com.soli.libcommon.R.color.B2)
                )
            )
        }
        binding.zoomImage.clickView { openActivity<TestAnotherTopSpecialActivity>() }

    }

    override fun initData() {

        ImageLoader.loadImage(
            binding.zoomImage,
            "https://dev-img01-joker.taihe.com/0209/M00/58/27/ChR47Fw9QpWABNZ4AALxJ3a2MZ8350.jpg"
        )
    }

}
package com.soli.newframeapp.demo

import android.support.design.widget.AppBarLayout
import com.soli.libCommon.base.BaseTopSpecialActivity
import com.soli.libCommon.util.ImageLoader
import com.soli.libCommon.util.MLog
import com.soli.libCommon.util.StatusBarUtil
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.activity_for_top_special_model.*

/**
 *
 * @author Soli
 * @Time 2018/11/15 11:17
 */
class TestTopSpecialActivity : BaseTopSpecialActivity() {

    override fun getContentView() = R.layout.activity_for_top_special_model

    override fun initView() {
        super.initView()
        title = "效果demo模块"
    }

    override fun initListener() {

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { layout, verticalOffset ->
            val scrollRangle = layout.totalScrollRange
            val alpha = Math.abs(verticalOffset) * 1.0 / scrollRangle * 1.0
            MLog.e("verticalOffset", alpha.toString())
            rootView.setToolbarBackgroudColor(
                StatusBarUtil.getColorWit1hAlpha1(
                    alpha,
                    ctx.resources.getColor(com.soli.libCommon.R.color.B2)
                )
            )
        })

    }

    override fun initData() {

        ImageLoader.loadImage(
            zoom_image,
            "https://nim.nosdn.127.net/MTAxMTAwMg==/bmltYV81NDU4MzMzMzc4XzE1NDAyNjIzOTQxNzZfODkwNDNlNDUtMGY2ZS00MTUzLTg3YTctYjBmNjExOTU2NWY4"
        )
    }

}
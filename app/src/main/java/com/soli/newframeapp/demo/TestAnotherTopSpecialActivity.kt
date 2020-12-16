package com.soli.newframeapp.demo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.android.material.appbar.AppBarLayout
import com.soli.libcommon.base.BaseTopSpecialActivity
import com.soli.libcommon.util.ImageLoader
import com.soli.libcommon.util.MLog
import com.soli.libcommon.util.StatusBarUtil
import com.soli.libcommon.util.noOpDelegate
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.activity_for_another_top_special_model.*
import kotlinx.android.synthetic.main.activity_for_top_special_model.appBarLayout
import kotlinx.android.synthetic.main.activity_for_top_special_model.zoom_image
import kotlinx.android.synthetic.main.content_layout.*

/**
 *
 * @author Soli
 * @Time 2018/11/15 11:17
 */
class TestAnotherTopSpecialActivity : BaseTopSpecialActivity() {

    private val tabFragment = ArrayList<Fragment>()

    override fun getContentView() = R.layout.activity_for_another_top_special_model

    override fun initView() {
        super.initView()
        title = "符合真实的一种情况测试"

        artViewPager2.offscreenPageLimit = 2
    }

    override fun initListener() {

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { layout, verticalOffset ->
            val scrollRangle = layout.totalScrollRange
            val alpha = Math.abs(verticalOffset) * 1.0 / scrollRangle * 1.0
            MLog.e("verticalOffset", alpha.toString())
            rootView.setToolbarBackgroudColor(
                StatusBarUtil.getColorWit1hAlpha1(
                    alpha,
                    ctx.resources.getColor(com.soli.libcommon.R.color.B2)
                )
            )
        })

        tabLayout.setOnTabSelectListener(object : OnTabSelectListener by noOpDelegate() {
            override fun onTabSelect(position: Int) {
                artViewPager2.currentItem = position
            }
        })

        artViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.currentTab = position
            }
        })
    }

    private class TabEntity(private val title: String) : CustomTabEntity {
        override fun getTabUnselectedIcon() = 0
        override fun getTabSelectedIcon() = 0
        override fun getTabTitle(): String = title
    }

    override fun initData() {

        ImageLoader.loadImage(
            zoom_image,
            "https://dev-img01-joker.taihe.com/0209/M00/58/27/ChR47Fw9QpWABNZ4AALxJ3a2MZ8350.jpg"
        )

        ImageLoader.loadImage(avator,"http://img01-joker.taihe.com/0209/M00/59/55/ChR47FycdmuAThMwAAtdi0_Ss5A481.png")

        setContentListData()
    }

    /**
     *
     */
    private fun setContentListData() {
        val tabList = ArrayList<TabEntity>().apply {
            add(TabEntity("热门单曲"))
            add(TabEntity("全部专辑"))
        }
        tabFragment.add(TestFragment())
        tabFragment.add(TestFragment2())

        tabLayout.setTabData(tabList as ArrayList<CustomTabEntity>)

        artViewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabFragment.size

            override fun createFragment(position: Int): Fragment = tabFragment[position]

        }

        artViewPager2.currentItem = 0
    }
}
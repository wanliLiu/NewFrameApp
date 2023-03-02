package com.soli.newframeapp.demo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.soli.libcommon.base.BaseTopSpecialActivity
import com.soli.libcommon.util.ImageLoader
import com.soli.libcommon.util.MLog
import com.soli.libcommon.util.StatusBarUtil
import com.soli.libcommon.util.noOpDelegate
import com.soli.libcommon.view.HeadImageView
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.ActivityForAnotherTopSpecialModelBinding

/**
 *
 * @author Soli
 * @Time 2018/11/15 11:17
 */
class TestAnotherTopSpecialActivity :
    BaseTopSpecialActivity<ActivityForAnotherTopSpecialModelBinding>() {

    private val tabFragment = ArrayList<Fragment>()

    override fun initView() {
        super.initView()
        title = "符合真实的一种情况测试"

        binding.artViewPager2.offscreenPageLimit = 2
    }

    override fun initListener() {

        binding.appBarLayout.addOnOffsetChangedListener { layout, verticalOffset ->
            val scrollRangle = layout.totalScrollRange
            val alpha = Math.abs(verticalOffset) * 1.0 / scrollRangle * 1.0
            MLog.e("verticalOffset", alpha.toString())
            rootView.setToolbarBackgroudColor(
                StatusBarUtil.getColorWit1hAlpha1(
                    alpha,
                    ctx.resources.getColor(com.soli.libcommon.R.color.B2)
                )
            )
        }

        binding.tabLayout.setOnTabSelectListener(object : OnTabSelectListener by noOpDelegate() {
            override fun onTabSelect(position: Int) {
                binding.artViewPager2.currentItem = position
            }
        })

        binding.artViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.currentTab = position
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
            binding.zoomImage,
            "https://dev-img01-joker.taihe.com/0209/M00/58/27/ChR47Fw9QpWABNZ4AALxJ3a2MZ8350.jpg"
        )

        ImageLoader.loadImage(
            binding.root.findViewById<HeadImageView>(R.id.avator),
            "http://img01-joker.taihe.com/0209/M00/59/55/ChR47FycdmuAThMwAAtdi0_Ss5A481.png"
        )

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

        binding.tabLayout.setTabData(tabList as ArrayList<CustomTabEntity>)

        binding.artViewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabFragment.size

            override fun createFragment(position: Int): Fragment = tabFragment[position]

        }

        binding.artViewPager2.currentItem = 0
    }
}
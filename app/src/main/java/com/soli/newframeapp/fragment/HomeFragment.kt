package com.soli.newframeapp.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.soli.libcommon.base.BaseFragment
import com.soli.libcommon.util.ViewListener
import com.soli.libcommon.view.SvgImageView
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.MainEntranceViewBinding
import me.yokeyword.fragmentation.ISupportFragment

/**
 *
 * @author Soli
 * @Time 2020/4/21 16:43
 */
class HomeFragment : BaseFragment<MainEntranceViewBinding>() {

    private var index = 0
    private var lastIndex = -1

    private val tabSize = 2
    private val tabFragments = arrayOfNulls<ISupportFragment>(tabSize)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        index = savedInstanceState?.getInt("index") ?: index
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("index", index)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index")
            checkItem()
        }
    }

    override fun initView() {
        val firstFragment = findChildFragment(TabHomeFragment::class.java)
        if (firstFragment == null) {
            tabFragments[0] = TabHomeFragment()
            tabFragments[1] = TabMeFragment()
            loadMultipleRootFragment(R.id.homeContainer, index, *tabFragments)
        } else {
            tabFragments[0] = firstFragment
            tabFragments[1] = findChildFragment(TabMeFragment::class.java)
        }
    }


    override fun initListener() {
        registerDoubleClickListener(binding.tabHome)
        registerDoubleClickListener(binding.tabMe)
    }

    override fun initData() {
        checkItem()
    }


    /**
     *
     */
    private fun onViewClick(v: View, isDoubleClick: Boolean = false) {
        when (v) {
            binding.tabHome -> index = 0
            binding.tabMe -> index = 1
        }
        if (!isDoubleClick) {
            checkItem()
        } else {
            tabFragments[index]?.apply {
                if (this is OnDoubleClickListener) (this as OnDoubleClickListener).onDoubleClickHappen()
            }
        }
    }

    /**
     *
     */
    private fun registerDoubleClickListener(clickView: View) {
        ViewListener.registerSelectDoubleClickListener(clickView, { view, isInCheckDoubleClick ->
            if (!isInCheckDoubleClick) onViewClick(view)
        }, { view ->
            onViewClick(view, true)
        })
    }


    /**
     *
     */
    private fun selectTab(tabIcon: SvgImageView, tabText: TextView, isSelect: Boolean) {
        val selectColor = if (isSelect) R.color.C1 else R.color.C3
        tabIcon.setImageColor(selectColor)
        tabText.setTextColor(ContextCompat.getColor(ctx!!.applicationContext, selectColor))
    }

    /**
     *
     */
    private fun checkItem() {

        selectTab(binding.tabHomeIcon, binding.tabHomeText, index == 0)

        selectTab(binding.tabMeIcon, binding.tabMeText, index == 1)

        if (lastIndex != index) {
            showHideFragment(
                tabFragments[index]!!,
                if (lastIndex == -1) tabFragments[index]!! else tabFragments[lastIndex]!!
            )
            lastIndex = index
        }
    }
}
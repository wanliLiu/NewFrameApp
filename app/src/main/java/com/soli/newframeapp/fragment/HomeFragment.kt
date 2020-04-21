package com.soli.newframeapp.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.soli.libcommon.util.TabFragmentManager
import com.soli.libcommon.util.ViewListener
import com.soli.libcommon.view.SvgImageView
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.main_entrance_view.*

/**
 *
 * @author Soli
 * @Time 2020/4/21 16:43
 */
class HomeFragment : BaseAnimationFragment() {

    private var index = 0
    private var lastIndex = -1

    private val tabManager by lazy {
        val tab = TabFragmentManager(ctx!! as AppCompatActivity, R.id.homeContainer)
        tab.addTab(0, TabHomeFragment::class.java, null)
        tab.addTab(1, TabMeFragment::class.java, null)
        tab
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        index = savedInstanceState?.getInt("index") ?: index
        super.onCreate(savedInstanceState)
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

    override fun getContentView() = R.layout.main_entrance_view

    override fun initView() {
    }


    override fun initListener() {
        registerDoubleClickListener(tabHome)
        registerDoubleClickListener(tabMe)

    }

    override fun initData() {
        checkItem()
    }

    /**
     *
     */
    private fun onViewClick(v: View, isDoubleClick: Boolean = false) {
        when (v) {
            tabHome -> index = 0
            tabMe -> index = 1
        }
        if (!isDoubleClick) {
            checkItem()
        } else {
            tabManager.getFragment(index)?.apply {
                if (this is OnDoubleClickListener)
                    (this as OnDoubleClickListener).onDoubleClickHappen()
            }
        }
    }

    /**
     *
     */
    private fun registerDoubleClickListener(clickView: View) {
        ViewListener.registerSelectDoubleClickListener(clickView, { view, isInCheckDoubleClick ->
            if (!isInCheckDoubleClick)
                onViewClick(view)
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

        selectTab(tabHomeIcon, tabHomeText, index == 0)

        selectTab(tabMeIcon, tabMeText, index == 1)

        if (lastIndex != index) {
            lastIndex = index
            tabManager.setCurrentTab(index)
        }
    }

    override fun onResume() {
        super.onResume()
        tabManager.getFragment(index)?.apply {
            this.Resume()
        }
    }
}
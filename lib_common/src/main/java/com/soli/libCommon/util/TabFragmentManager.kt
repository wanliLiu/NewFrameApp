package com.soli.libCommon.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.soli.libCommon.base.BaseFragment

/**
 * Tab 的framgent管理，前提事BaseFragment
 * @author Soli
 * @Time 18-5-17 上午10:58
 */
open class TabFragmentManager(activity: AppCompatActivity, containerId: Int) {

    private val mActivity = activity
    private val mContainerId = containerId

    private val mtabs: LinkedHashMap<String, TabInfo> = LinkedHashMap()

    private data class TabInfo(val tag: String, val clss: Class<out BaseFragment>, val args: Bundle? = null, var fragment: BaseFragment? = null)

    /**
     * 添加tab
     */
    fun addTab(id: Int, clss: Class<out BaseFragment>, args: Bundle? = null) {
        val tag = id.toString()
        val info = TabInfo(tag, clss, args)
        val fragment = mActivity.supportFragmentManager.findFragmentByTag(tag)
        fragment?.apply { info.fragment = this as BaseFragment }
        mtabs[tag] = info
    }

    /**
     * 设置当前显示的页
     */
    fun setCurrentTab(id: Int) {

        //先隐藏调
        mtabs.forEach {
            val tabinfo = it.value
            if (it.key != id.toString() && tabinfo.fragment != null) {
                val ft = mActivity.supportFragmentManager.beginTransaction()
                tabinfo.fragment!!.onPause()
                ft.hide(tabinfo.fragment!!)
                ft.commitAllowingStateLoss()
            }
        }
        showTab(id)
    }

    /**
     *
     */
    private fun showTab(id: Int) {
        if (mActivity.isFinishing)
            return

        mtabs[id.toString()]?.apply {
            val ft = mActivity.supportFragmentManager.beginTransaction()
            if (fragment == null) {
                fragment = androidx.fragment.app.Fragment.instantiate(mActivity, clss.name, args) as BaseFragment
                ft.add(mContainerId, fragment!!, tag)
                fragment!!.Resume()
            } else {
                ft.show(fragment!!)
                fragment!!.Resume()
            }
            ft.commitAllowingStateLoss()
        }
    }

    /**
     *
     */
    fun getFragment(id: Int): BaseFragment? = mtabs.get(id.toString())?.fragment

    /**
     * fragment添加到Acitiivity的数量
     */
    fun getAddedNum() = {
        var num = 0
        mtabs.forEach {
            val tabInfo = it.value
            tabInfo.fragment?.apply {
                if (isAdded) num++
            }
        }
        num
    }
}
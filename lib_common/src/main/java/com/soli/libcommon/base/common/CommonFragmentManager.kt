package com.soli.libcommon.base.common

import me.yokeyword.fragmentation.SupportFragment

/**
 *  单一Activity ，承载Fragment的Fragment管理,目前先简单这样弄
 * @author Soli
 * @Time 2020/5/20 14:48
 */
class CommonFragmentManager {

    private val fragmentStack = mutableMapOf<Long, SupportFragment>()

    companion object {
        val Instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { CommonFragmentManager() }
    }


    /**
     *
     */
    fun addFragment(fragment: SupportFragment) =
        with(System.currentTimeMillis()) {
            fragmentStack[this] = fragment
            this
        }

    operator fun get(tag: Long) = fragmentStack[tag]

    /**
     *
     */
    fun popFragment(tag: Long) = fragmentStack.remove(tag)
}
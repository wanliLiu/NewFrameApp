package com.soli.newframeapp.fragment

import android.widget.Toast
import com.soli.libcommon.base.BaseFragmentationFragment

/*
 * @author soli
 * @Time 2020/4/21 21:57
 */

abstract class BaseTabHomeFragment : BaseFragmentationFragment(),OnDoubleClickListener {

//    // 再点一次退出程序时间设置
//    private val WAIT_TIME = 2000L
//    private var TOUCH_TIME: Long = 0
//
//    /**
//     * 处理回退事件
//     *
//     * @return
//     */
//    override fun onBackPressedSupport(): Boolean {
//        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
//            _mActivity!!.finish()
//        } else {
//            TOUCH_TIME = System.currentTimeMillis()
//            Toast.makeText(ctx, "再按一次退出程序！", Toast.LENGTH_SHORT).show()
//        }
//        return true
//    }

    override fun onDoubleClickHappen() {
    }
}
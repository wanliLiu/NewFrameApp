package com.soli.lib_common.view.root

/**
 * 获取网络数据失败调用接口
 *
 * @author milanoouser
 */
interface DataErrorCallBack {

    /**
     * 在一次获取数据
     */
    fun onRetry()
}

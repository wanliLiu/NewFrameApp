package com.soli.lib_common.base

import com.soli.lib_common.view.root.LoadingType

/**
 * @author Soli
 * @Time 18-5-15 上午11:37
 */
open interface BaseInterface {

    fun showProgress(show: Boolean)
    /**
     *
     */
    fun showProgress()

    /**
     *
     */
    fun showProgress(type: LoadingType)

    /**
     *
     */
    fun dismissProgress()
}
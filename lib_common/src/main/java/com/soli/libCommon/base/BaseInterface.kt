package com.soli.libCommon.base

import com.soli.libCommon.view.root.LoadingType

/**
 * @author Soli
 * @Time 18-5-15 上午11:37
 */
interface BaseInterface {

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
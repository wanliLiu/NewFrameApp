package com.soli.libcommon.base

import com.soli.libcommon.view.loading.LoadingType

/**
 * @author Soli
 * @Time 18-5-15 上午11:37
 */
interface BaseInterface {
    /**
     *
     */
    fun showProgress(show: Boolean = true, cancle: Boolean = true, type: LoadingType = LoadingType.TypeInside)

    /**
     *
     */
    fun dismissProgress()
}
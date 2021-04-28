package com.soli.libcommon.view.web

import java.util.ArrayList

/**
 *
 * <p>
 * Created by sofia on 4/28/2021.
 */
interface OnWebImageClickListener {
    /**
     *
     */
    fun onImageClick(currentPosition: Int, urls: ArrayList<String>)

    /**
     *
     */
    fun onUrlClick(url: String?)
}
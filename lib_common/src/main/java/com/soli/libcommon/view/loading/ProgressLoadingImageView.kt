package com.soli.libcommon.view.loading

import android.content.Context
import android.util.AttributeSet

/**
 *
 * @author Soli
 * @Time 2020/5/27 14:56
 */
class ProgressLoadingImageView : PullLoadingImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        //showprogress的时候，有时候不会不动的问题，stepfragme估计有问题，所以这种呢情况下就默认都动
        initStartPlay = true
    }
}
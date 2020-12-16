package com.soli.libcommon.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.soli.libcommon.util.MLog

/**
 *
 * @author Soli
 * @Time 2019/2/20 11:25
 */
class TopBarEmptyView : View {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var needAutoSet = true

    init {
        setOnApplyWindowInsetsListener { _, insets ->
            if (needAutoSet) {
                changeLayoutParams(insets.systemWindowInsetTop)
                MLog.e("onApplyWindowInsets", "$insets")
            }
            return@setOnApplyWindowInsetsListener insets
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (needAutoSet)
            changeLayoutParams(rootWindowInsets?.systemWindowInsetTop ?: 0)
    }


    fun changeLayoutParams(height: Int = 0) {
        visibility = if (height == 0) {
            GONE
        } else VISIBLE
        val params = this.layoutParams
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = height
            this.layoutParams = params
        }
    }
}
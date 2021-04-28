package com.soli.libcommon.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 *
 * @author Soli
 * @Time 2020/7/20 14:05
 */
class RootFrameLayout : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var needDoOnDispatch: (() -> Unit)? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        needDoOnDispatch?.invoke()
        return super.dispatchTouchEvent(ev)
    }
}
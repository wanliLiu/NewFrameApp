package com.soli.newframeapp.demo

import android.content.Context
import android.util.AttributeSet
import android.view.ViewParent
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

/**
 *
 * @author Soli
 * @Time 12/16/20 4:25 PM
 */
class ContentMotionLayout : MotionLayout, AppBarLayout.OnOffsetChangedListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        progress = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findAppBarLayout(parent)?.addOnOffsetChangedListener(this)
    }

    /**
     *
     */
    private fun findAppBarLayout(parent: ViewParent?): AppBarLayout? {
        parent ?: return null
        val mParent = parent
        return if (mParent is AppBarLayout)
            mParent
        else
            findAppBarLayout(mParent.parent)
    }

}
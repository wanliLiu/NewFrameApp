package com.soli.libcommon.view

import android.view.View
import androidx.core.view.ViewCompat

/**
 *
 * @author Soli
 * @Time 2017/9/27
 */
class ViewOffsetHelper(private val mView: View) {

    var layoutTop: Int = 0
        private set
    var layoutLeft: Int = 0
        private set
    private var mOffsetTop: Int = 0
    private var mOffsetLeft: Int = 0

    fun onViewLayout() {
        // Now grab the intended top
        layoutTop = mView.top
        layoutLeft = mView.left

        // And offset it as needed
        updateOffsets()
    }

    private fun updateOffsets() {
        ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.top - layoutTop))
        ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.left - layoutLeft))
    }

    /**
     * Set the top and bottom offset for this [ViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (mOffsetTop != offset) {
            mOffsetTop = offset
            updateOffsets()
            return true
        }
        return false
    }

    /**
     * Set the left and right offset for this [ViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (mOffsetLeft != offset) {
            mOffsetLeft = offset
            updateOffsets()
            return true
        }
        return false
    }

    fun getTopAndBottomOffset(): Int {
        return mOffsetTop
    }

    fun getLeftAndRightOffset(): Int {
        return mOffsetLeft
    }
}

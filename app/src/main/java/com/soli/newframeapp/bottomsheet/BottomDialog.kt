package com.soli.newframeapp.bottomsheet

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window

/**
 * @author Soli
 * @Time 2018/11/12 15:02
 */
class BottomDialog : BottomSheetDialog {

    private var mPeekHeight: Int = 0
    private var mMaxHeight: Int = 0
    private var mCreated: Boolean = false
    private var mWindow: Window? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null

    private// setContentView() 没有调用
    val bottomSheetBehavior: BottomSheetBehavior<*>?
        get() {
            if (mBottomSheetBehavior != null) {
                return mBottomSheetBehavior
            }

            val view = mWindow!!.findViewById<View>(android.support.design.R.id.design_bottom_sheet) ?: return null
            mBottomSheetBehavior = BottomSheetBehavior.from(view)
            return mBottomSheetBehavior
        }

    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(
            bottomSheet: View,
            @BottomSheetBehavior.State newState: Int
        ) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    constructor(context: Context, peekHeight: Int, maxHeight: Int) : super(context) {
        init(peekHeight, maxHeight)
    }

    constructor(context: Context, theme: Int, peekHeight: Int, maxHeight: Int) : super(context, theme) {
        init(peekHeight, maxHeight)
    }

//    constructor(
//        context: Context,
//        cancelable: Boolean,
//        cancelListener: DialogInterface.OnCancelListener,
//        peekHeight: Int,
//        maxHeight: Int
//    ) : super(context, cancelable, cancelListener) {
//        init(peekHeight, maxHeight)
//    }

    private fun init(peekHeight: Int, maxHeight: Int) {
        mWindow = window
        mPeekHeight = peekHeight
        mMaxHeight = maxHeight
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)

        mCreated = true

        setPeekHeight()
        setMaxHeight()
        setBottomSheetCallback()
    }

    fun setPeekHeight(peekHeight: Int) {
        mPeekHeight = peekHeight

        if (mCreated) {
            setPeekHeight()
        }
    }

    fun setMaxHeight(height: Int) {
        mMaxHeight = height

        if (mCreated) {
            setMaxHeight()
        }
    }

    fun setBatterSwipeDismiss(enabled: Boolean) {
        if (enabled) {

        }
    }

    private fun setPeekHeight() {
        if (mPeekHeight <= 0) {
            return
        }

        if (bottomSheetBehavior != null) {
            mBottomSheetBehavior!!.peekHeight = mPeekHeight
        }
    }

    private fun setMaxHeight() {
        if (mMaxHeight <= 0) {
            return
        }

        mWindow!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mMaxHeight)
        mWindow!!.setGravity(Gravity.BOTTOM)
    }

    private fun setBottomSheetCallback() {
        if (bottomSheetBehavior != null) {
            mBottomSheetBehavior!!.setBottomSheetCallback(mBottomSheetCallback)
        }
    }
}

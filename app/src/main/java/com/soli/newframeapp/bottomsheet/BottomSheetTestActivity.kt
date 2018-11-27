package com.soli.newframeapp.bottomsheet

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.view.View
import android.widget.FrameLayout
import com.soli.libCommon.base.BaseActivity
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.activity_bottom_sheet.*

/**
 *
 * @author Soli
 * @Time 2018/11/12 09:53
 */
class BottomSheetTestActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_bottom_sheet

    override fun initView() {
        title = "BottomSheet"
    }

    override fun initListener() {

        btnSheetDialog.setOnClickListener {
            showBottomSheetDialog()
        }

        btnSheetFragmentDialog.setOnClickListener {
            BottomSheetFragment.instance.show(supportFragmentManager, "dialog")
        }
    }

    override fun initData() {
    }

    /**
     *
     */
    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(ctx, R.style.TransBottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.view_bottomsheet, null)
        dialog.setContentView(view)
        view.findViewById<View>(R.id.tst).setOnClickListener {   BottomSheetFragment.instance.show(supportFragmentManager, "dialog") }

        val behavior =
            BottomSheetBehavior.from(dialog.delegate.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet))
        behavior.peekHeight = 0
        behavior.isHideable = true
        behavior.skipCollapsed = true
        dialog.show()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


    }
}
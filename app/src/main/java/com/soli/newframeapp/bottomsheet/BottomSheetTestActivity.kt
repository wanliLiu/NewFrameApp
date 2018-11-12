package com.soli.newframeapp.bottomsheet

import android.support.design.widget.BottomSheetDialog
import android.view.View
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
        val dialog = BottomSheetDialog(ctx,R.style.TransBottomSheetDialogStyle)
        val view = layoutInflater.inflate(R.layout.view_bottomsheet, null)
        dialog.setContentView(view)
        dialog.show()

       dialog.delegate.findViewById<View>(android.support.design.R.id.container)?.fitsSystemWindows = false
        dialog.delegate.findViewById<View>(android.support.design.R.id.coordinator)?.fitsSystemWindows = false
    }
}
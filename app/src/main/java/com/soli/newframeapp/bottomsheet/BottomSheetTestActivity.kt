package com.soli.newframeapp.bottomsheet

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.soli.libcommon.base.BaseActivity
import com.soli.libcommon.bottomsheet.BottomDialog
import com.soli.newframeapp.R
import com.soli.newframeapp.databinding.ActivityBottomSheetBinding

/**
 *
 * @author Soli
 * @Time 2018/11/12 09:53
 */
class BottomSheetTestActivity : BaseActivity<ActivityBottomSheetBinding>() {

    private var vomView : View?= null

    private val inputDialog by lazy {
        BottomDialog(ctx as Context).apply {
            val view = layoutInflater.inflate(R.layout.view_bottomsheet, null)
            val te = view.findViewById<TextView>(R.id.bNewtn)
            te.text = "输入评论"
            setContentView(view)
        }
    }

    override fun initView() {
        title = "BottomSheet"
    }

    override fun initListener() {

        inputDialog.setOnDismissListener {
            vomView?.findViewById<FrameLayout>(R.id.background)?.setBackgroundResource(R.drawable.bottom_sheet_background)
        }
        binding.btnSheetDialog.setOnClickListener {
            val commDialog = BottomDialog(ctx as Context)
            commDialog.topOffsetDefault()

            vomView = layoutInflater.inflate(R.layout.view_bottomsheet, null)
            val te = vomView!!.findViewById<TextView>(R.id.bNewtn)
            te.text = "弹起的评论"
            te.setOnClickListener {
                vomView!!.findViewById<FrameLayout>(R.id.background).setBackgroundResource(R.drawable.bottom_sheet_background_open)
                inputDialog.show()
            }

            commDialog.setContentView(vomView!!)
            commDialog.show()
        }

        binding.btnSheetFragmentDialog.setOnClickListener {
            BottomSheetFragment.instance.show(supportFragmentManager, "dialog")
        }
    }

    override fun initData() {
    }
}
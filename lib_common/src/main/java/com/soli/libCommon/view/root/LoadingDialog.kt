package com.soli.libCommon.view.root

import android.app.Dialog
import android.content.Context
import android.view.View
import com.soli.libCommon.R
import com.soli.libCommon.view.PullLoadingImageView


/**
 * 加载进度条
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.CustomProgressDialog) {

    private val imageView: PullLoadingImageView

    init {
        val contentView = View.inflate(context, R.layout.loding_inside_dialog, null)
        setContentView(contentView)

        val lp = window!!.attributes
        lp.dimAmount = 0.0f
        window!!.attributes = lp

        imageView = contentView.findViewById(R.id.image_anim)
        imageView.isAutoAnimation = false

        setOnDismissListener { imageView.stopAnim() }
    }

    override fun show() {
        imageView.startAnim()
        super.show()
    }


}

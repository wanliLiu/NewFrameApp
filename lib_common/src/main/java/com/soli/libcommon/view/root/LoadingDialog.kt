package com.soli.libcommon.view.root

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.soli.libcommon.R
import com.soli.libcommon.view.PullLoadingImageView
import com.soli.libcommon.view.RingProgressBar


/**
 * 加载进度条
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.CustomProgressDialog) {

    private val imageView: PullLoadingImageView
    private val progBarView: RingProgressBar

    init {
        val contentView = View.inflate(context, R.layout.loding_inside_dialog, null)
        setContentView(contentView)

        val lp = window!!.attributes
        lp.dimAmount = 0.0f
        window!!.attributes = lp

        imageView = contentView.findViewById(R.id.image_anim)
        progBarView = contentView.findViewById(R.id.progBarView)
        progBarView.ringProgressColor = ContextCompat.getColor(context, R.color.A1)
        progBarView.ringColor = ContextCompat.getColor(context, R.color.A1)
        progBarView.visibility = View.GONE

        setOnDismissListener { imageView.stopAnim() }
    }

    /**
     *
     */
    fun showNumProgress(show: Boolean = true) {
        imageView.visibility = if (show) View.INVISIBLE else View.VISIBLE
        progBarView.visibility = if (show) View.VISIBLE else View.GONE
        if (show)
            progBarView.progress = 0
    }

    /**
     *
     */
    fun setProgress(progress: Int) {
        progBarView.progress = progress
    }
}

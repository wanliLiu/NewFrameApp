package com.soli.libcommon.view.loading

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.soli.libcommon.R
import com.soli.libcommon.view.RingProgressBar


/**
 *
 * <p>
 * Created by sofia on 4/28/2021.
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.CustomProgressDialog) {

    private val imageView: ProgressLoadingImageView
    private val progBarView: RingProgressBar

    init {
        val contentView = View.inflate(context, R.layout.loding_inside_dialog, null)
        setContentView(contentView)

        val lp = window!!.attributes
        lp.dimAmount = 0.0f
        window!!.attributes = lp

        imageView = contentView.findViewById(R.id.loadingImageView)
        progBarView = contentView.findViewById(R.id.progBarView)
        progBarView.ringProgressColor = ContextCompat.getColor(context, R.color.A1)
        progBarView.ringColor = ContextCompat.getColor(context, R.color.A1)
        progBarView.visibility = View.GONE

        setOnDismissListener { imageView.stopAnim() }
    }

    override fun dismiss() {
        imageView.stopAnim()
        super.dismiss()
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


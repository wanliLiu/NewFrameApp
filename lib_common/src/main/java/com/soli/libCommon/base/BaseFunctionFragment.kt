package com.soli.libCommon.base

import android.app.ProgressDialog
import android.os.Handler
import android.support.v4.app.Fragment

/**
 * @author Soli
 * @Time 18-5-16 上午11:10
 */
abstract class BaseFunctionFragment : Fragment(), BaseInterface {
    /**
     * 上下午context
     */
    protected val ctx by lazy { this.activity }

    private var dialog: ProgressDialog? = null

    private val handler = Handler()


    /**
     *
     */
    fun showProgressDialog() {
        if (dialog == null) {
            dialog = ProgressDialog(ctx)
            dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }

        if (!dialog!!.isShowing)
            dialog!!.show()
    }

    /**
     *
     */
    fun dissProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    override fun onPause() {
        super.onPause()
        dissProgressDialog()
    }

    protected fun getHandler(): Handler {
        return handler
    }

    protected fun postRunnable(runnable: Runnable) {
        handler.post(Runnable {
            // validate
            // TODO use getActivity ?
            if (!isAdded) {
                return@Runnable
            }

            // run
            runnable.run()
        })
    }
}
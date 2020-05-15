package com.soli.libcommon.base

import android.app.ProgressDialog
import android.os.Handler
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment

/**
 * @author Soli
 * @Time 18-5-16 上午11:10
 */
abstract class BaseFunctionFragment : SwipeBackFragment(), BaseInterface {
    /**
     * 上下午context
     */
    protected val ctx by lazy { this.activity }

    private var dialog: ProgressDialog? = null

    private val handler = Handler()


    /**
     *
     */
    fun showProgressDialog(cancle: Boolean = true) {
        val parent = activity
        if (parent != null && parent is BaseFunctionActivity)
            parent.showProgressDialog(cancle)
        childFragmentManager
    }

    /**
     *
     */
    fun dissProgressDialog() {
        val parent = activity
        if (parent != null && parent is BaseFunctionActivity)
            parent.dissProgressDialog()
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
package com.soli.libCommon.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * @author Soli
 * @Time 18-5-31 下午4:20
 */
open class RecyclerViewEmpty(ctx: Context, attrs: AttributeSet?, defstyle: Int) : RecyclerView(ctx, attrs, defstyle) {

    private var emptyView: View? = null
    private val observer = dataObserver()

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    /**
     *
     */
    override fun setAdapter(adapter: Adapter<*>?) {

        getAdapter()?.unregisterAdapterDataObserver(observer)

        super.setAdapter(adapter)

        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }

    /**
     * 设置空视图
     * @param view 需要设置的空视图
     */
    fun setEmptyView(view: View) {
        emptyView = view
        checkIfEmpty()
    }

    /**
     *
     */
    private fun checkIfEmpty() {
        if (emptyView != null && adapter != null) {
            emptyView?.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }


    private inner class dataObserver : AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()
            checkIfEmpty()
        }

//        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
//            super.onItemRangeChanged(positionStart, itemCount)
//            checkIfEmpty()
//        }
//
//        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
//            super.onItemRangeChanged(positionStart, itemCount, payload)
//            checkIfEmpty()
//        }
//
//        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//            super.onItemRangeInserted(positionStart, itemCount)
//            checkIfEmpty()
//        }
//
//        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
//            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
//            checkIfEmpty()
//        }
//
//        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
//            super.onItemRangeRemoved(positionStart, itemCount)
//            checkIfEmpty()
//        }
    }

}
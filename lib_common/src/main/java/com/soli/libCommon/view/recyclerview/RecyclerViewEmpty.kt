package com.soli.libCommon.view.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.soli.pullupdownrefresh.more.LoadMoreRecyclerAdapter

/**
 * @author Soli
 * @Time 18-5-31 下午4:20
 */
open class RecyclerViewEmpty(ctx: Context, attrs: AttributeSet?, defstyle: Int) : androidx.recyclerview.widget.RecyclerView(ctx, attrs, defstyle) {

    private var emptyView: View? = null
    private val observer = dataObserver()

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    /**
     *
     */
    override fun setAdapter(adapter: Adapter<*>?) {
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
            val count = if (adapter is LoadMoreRecyclerAdapter)
                (adapter as LoadMoreRecyclerAdapter).itemCountHF
            else
                adapter?.itemCount ?: 0

            emptyView?.visibility = if (count == 0) View.VISIBLE else View.GONE
        }
    }


    private inner class dataObserver : AdapterDataObserver() {

        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

}
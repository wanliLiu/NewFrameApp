package com.taihe.libCommon.view.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.soli.pullupdownrefresh.more.LoadMoreRecyclerAdapter
import com.soli.pullupdownrefresh.more.LoadMoreRecyclerAdapter.TYPE_MANAGER_STAGGERED_GRID

/*
 * 圈子的列表的视图组件
 * @author soli
 * @Time 2018/12/3 20:39
 */
open class LoadMoreRecyclerView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerViewEmpty(context, attrs, defStyleAttr) {

    private var mangerType = 0

    private var headview: View? = null
    private var footview: View? = null

    private var loadMoreAdapter: LoadMoreRecyclerAdapter? = null

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)


    override fun setAdapter(adapter: Adapter<*>?) {
        loadMoreAdapter = LoadMoreRecyclerAdapter(adapter as Adapter<ViewHolder>?)

        loadMoreAdapter!!.setmManagerType(mangerType)

        if (headview != null)
            loadMoreAdapter!!.addHeader(headview)

        if (footview != null)
            loadMoreAdapter!!.addFooter(footview)

        super.setAdapter(loadMoreAdapter)
    }

    fun isHeader(position: Int) = loadMoreAdapter?.isHeader(position) ?: false

    fun isFooter(position: Int) = loadMoreAdapter?.isFooter(position) ?: false


    fun markIsStaggeredGridLayoutManager() {
        mangerType = TYPE_MANAGER_STAGGERED_GRID
        loadMoreAdapter?.setmManagerType(mangerType)
    }

    fun getHeader() = headview

    fun getFooter() = footview

    /**
     *
     */
    fun addHeader(view: View) {
        headview = view
        loadMoreAdapter?.addHeader(headview)
    }

    fun addFooter(view: View) {
        footview = view
        loadMoreAdapter?.addFooter(footview)
    }
}
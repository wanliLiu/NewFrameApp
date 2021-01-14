package com.soli.libcommon.view.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.wechat.moments.view.HeaderFooterAdapter

/**
 * 用于支持有自定义顶部和脚部的功能
 * @author Soli
 * @Time 1/14/21 5:21 PM
 */
open class HeaderFooterRecyclerView : RecyclerViewEmpty {

    private var mAdapter: HeaderFooterAdapter? = null

    val parentAdapter: HeaderFooterAdapter?
        get() = mAdapter

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun setAdapter(adapter: Adapter<*>?) {
        mAdapter = HeaderFooterAdapter(adapter as Adapter<ViewHolder>)
        super.setAdapter(mAdapter)
    }

    fun addHeader(view: View) {
        mAdapter?.addHeader(view)
    }

    /**
     *
     */
    fun getHeaderViewForPosition(pos: Int) = mAdapter?.getHeaderViewForPosition(pos)

    fun getFooterViewForPosition(pos: Int) = mAdapter?.getFooterViewForPosition(pos)

    fun removeHeader(view: View) {
        mAdapter?.removeHeader(view)
    }

    fun addFooter(view: View) {
        mAdapter?.addFooter(view)
    }

    fun removeFooter(view: View) {
        mAdapter?.removeFooter(view)
    }
}
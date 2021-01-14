package com.soli.libcommon.view.recyclerview

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

/**
 *  用来中专一下
 * @author Soli
 * @Time 1/14/21 3:00 PM
 */
class HeaderFooterAdapter(private val mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ItemTypeHeader = 0
        private const val ItemTypeContent = 1
        private const val ItemTypeFooter = 2
    }

    private val mHeaderView = mutableListOf<View>()
    private val mFooterView = mutableListOf<View>()

    private val hasHeader: Boolean
        get() = mHeaderView.isNotEmpty()
    private val headerCount: Int
        get() = mHeaderView.size

    private val hasFooter: Boolean
        get() = mFooterView.isNotEmpty()
    private val footerCount: Int
        get() = mFooterView.size

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            notifyItemMoved(fromPosition + headerCount, toPosition + headerCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(positionStart + headerCount, itemCount)
        }
    }


    init {
        mAdapter.registerAdapterDataObserver(adapterDataObserver)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mAdapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mAdapter.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        mAdapter.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return mAdapter.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        mAdapter.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        mAdapter.onViewDetachedFromWindow(holder)
    }

    fun inHeader(pos: Int) = pos < mHeaderView.size

    fun inFooter(pos: Int) = pos >= (mHeaderView.size + mAdapter.itemCount)

    override fun getItemViewType(position: Int) =
        when {
            hasHeader && inHeader(position) -> ItemTypeHeader
            hasFooter && inFooter(position) -> ItemTypeFooter
            else -> ItemTypeContent
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemTypeHeader, ItemTypeFooter -> HeaderFooterViewHolder(parent)
            ItemTypeContent -> mAdapter.onCreateViewHolder(parent, viewType)
            else -> throw IllegalArgumentException("错误的ViewType类型 ：$viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        throw IllegalAccessException("不用这个，直接用payload那种")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (getItemViewType(position)) {
            ItemTypeHeader -> (holder as? HeaderFooterViewHolder)?.addView(mHeaderView[position])
            ItemTypeFooter -> (holder as? HeaderFooterViewHolder)?.addView(
                mFooterView[footPosition(position)]
            )
            ItemTypeContent -> mAdapter.onBindViewHolder(holder, position - headerCount, payloads)
        }
    }


    private class HeaderFooterViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }) {

        /**
         *
         */
        fun addView(view: View) {

            if (view.parent != null) {
                (view.parent as ViewGroup).removeView(view)
            }

            (itemView as? FrameLayout)?.apply {
                removeAllViews()
                addView(
                    view,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
    }

    override fun getItemCount() = mAdapter.itemCount + headerCount + footerCount

    override fun getItemId(position: Int): Long = mAdapter.getItemId(position - headerCount)

    fun addHeader(view: View) {
        if (!mHeaderView.contains(view)) {
            mHeaderView += view
            notifyItemInserted(mHeaderView.indexOf(view))
        }
    }

    private fun footPosition(pos: Int) = pos - headerCount - mAdapter.itemCount

    /**
     *
     */
    fun getHeaderViewForPosition(pos: Int) = if (inHeader(pos)) mHeaderView[pos] else null

    fun getFooterViewForPosition(pos: Int) =
        if (inFooter(pos)) mHeaderView[footPosition(pos)] else null

    fun removeHeader(view: View) {
        val index = mHeaderView.indexOf(view)
        if (index != -1) {
            mHeaderView.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun addFooter(view: View) {
        if (!mFooterView.contains(view)) {
            mFooterView += view
            notifyItemInserted(mAdapter.itemCount + headerCount + mFooterView.indexOf(view))
        }
    }

    fun removeFooter(view: View) {
        val index = mFooterView.indexOf(view)
        if (index != -1) {
            mFooterView.removeAt(index)
            notifyItemRemoved(mAdapter.itemCount + headerCount + index)
        }
    }
}
package com.soli.libcommon.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.soli.libcommon.util.clickView
import java.lang.reflect.ParameterizedType

/**
 *
 * @author Soli
 * @Time 2020/4/24 13:42
 */
abstract class BaseRecycleAdapter<T, Binding : ViewBinding>(
    context: Context, list: MutableList<T>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor(context: Context) : this(context, null)

    val ITEM_TYPE_NORMAL = 0
    val ITEM_TYPE_HEADER = 1
    val ITEM_TYPE_FOOTER = 2

    private var mList: MutableList<T>? = list

    val ctx: Context = context

    var headerView: View? = null
    var footerView: View? = null

    protected val inflater = LayoutInflater.from(context)

    //更新数据是否要用有动画的那种效果
    var useHaveAnimationRefresh = true

    //item的点击事件
    var onItemClickListener: ((view: View, position: Int, data: T?) -> Unit)? = null


    /***
     *
     */
    private fun initBindView(): Binding {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[1] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, inflater) as Binding
    }


    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        return onCreateView(viewGroup, viewType)
    }

    override fun onBindViewHolder(
        mholder: RecyclerView.ViewHolder, position: Int
    ) {
        onBindViewHolder(mholder, position, arrayListOf())
    }

    override fun onBindViewHolder(
        mholder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        val type = getItemViewType(position)
        val realPosition = getRealItemPosition(position)
        if (ITEM_TYPE_NORMAL == type) {
            onBindView(
                (mholder as BaseViewHolder<Binding>).binding, type, position, realPosition, payloads
            )
            //注意如果 onBindView里面也设置了点击时间，并且这里不为空，那么onBindView里面设置的就会不管用
            if (onItemClickListener != null) {
                mholder.itemView.clickView {
                    onItemClickListener!!(it, realPosition, getItemData(realPosition))
                }
            }
        }
    }

    open fun onCreateView(
        parent: ViewGroup?, viewType: Int
    ): RecyclerView.ViewHolder {
        return BaseViewHolder(initBindView())
    }

    /**
     * 加了header后，position会有所不同,以下是说明
     *
     * @param itemType          判断是headview，footerView的标识
     * @param original_position 原始的position
     * @param real_position     真正的position，
     */
    protected abstract fun onBindView(
        binding: Binding,
        itemType: Int,
        originalPosition: Int,
        realPosition: Int,
        payloads: List<Any>
    )

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }


    /**
     * 获取真正的position,因为加了头部与底部，position会有所位移
     */
    fun getRealItemPosition(position: Int): Int {
        return if (null != headerView) {
            position - 1
        } else position
    }

    /**
     * @return
     */
    val realItemCount: Int
        get() = if (mList == null) 0 else mList!!.size

    override fun getItemCount(): Int {
        return realItemCount + headerCount + footerCount
    }

    override fun getItemViewType(position: Int): Int {
        if (headerView != null && position == 0) {
            return ITEM_TYPE_HEADER
        } else if (footerView != null && itemCount - 1 == position) {
            return ITEM_TYPE_FOOTER
        }
        return ITEM_TYPE_NORMAL
    }

    /**
     * @param position
     * @return
     */
    fun getItemData(position: Int): T? {
        return if (mList != null && mList!!.size > 0 && position < realItemCount) mList!![position] else null
    }

    /**
     * @param list
     */
    var list: MutableList<T>
        get() = checkList()
        set(value) {
            checkList()
            mList!!.clear()
            mList!!.addAll(value!!)
            doSomethingWhenSetList()
            notifyDataSetChanged()
        }

    open fun doSomethingWhenSetList() {}

    /**
     *
     */
    private fun checkList(): MutableList<T> {
        if (mList == null) {
            mList = ArrayList()
        }
        return mList!!
    }

    fun add(t: T) {
        checkList()
        mList!!.add(t)
        if (useHaveAnimationRefresh) notifyItemInserted(mList!!.size - 1 + headerCount) else notifyDataSetChanged()
    }

    val headerCount: Int
        get() = if (headerView != null) 1 else 0

    val footerCount: Int
        get() = if (footerView != null) 1 else 0

    /**
     * insert  a item associated with the specified position of adapter
     *
     * @param position
     * @param item
     */
    fun add(position: Int, item: T) {
        checkList()
        mList!!.add(position, item)
        if (useHaveAnimationRefresh) {
            notifyItemInserted(position + headerCount)
            notifyItemRangeChanged(
                position + headerCount, itemCount - position - headerCount
            )
        } else notifyDataSetChanged()
    }

    fun addAll(newData: List<T>?) {
        if (newData == null || newData.isEmpty()) return
        checkList()
        mList!!.addAll(newData)
        if (useHaveAnimationRefresh) {
            notifyItemRangeInserted(mList!!.size - newData.size + headerCount, newData.size)
        } else notifyDataSetChanged()
    }

    fun addAll_Range(newData: List<T>?) {
        if (newData == null || newData.isEmpty()) return
        checkList()
        mList!!.addAll(newData)
        notifyItemRangeChanged(mList!!.size - newData.size + headerCount, newData.size)
    }

    fun addAll(position: Int, newData: List<T>?) {
        if (newData == null || newData.isEmpty()) return
        checkList()
        mList!!.addAll(position, newData)
        if (useHaveAnimationRefresh) {
            notifyItemRangeInserted(position + headerCount, newData.size)
            notifyItemRangeChanged(
                position + headerCount, itemCount - position - headerCount
            )
        } else notifyDataSetChanged()
    }

    fun insertAtTop(data: T) {
        insert(0, data)
    }

    /**
     * @param position
     * @param data
     */
    fun insert(position: Int, data: T) {
        checkList()
        mList!!.add(position, data)
        if (useHaveAnimationRefresh) {
            notifyItemInserted(position + headerCount)
            notifyItemRangeChanged(
                position + headerCount, itemCount - position - headerCount
            )
        } else notifyDataSetChanged()
    }

    /**
     * @param position
     * @param data
     */
    operator fun set(position: Int, data: T) {
        if (mList != null && position < itemCount) {
            mList!![position] = data
            notifyItemChanged(position)
        }
    }

    /**
     * @param position
     */
    fun remove(position: Int) {
        try {
            if (mList != null) {
                if (position > -1 && position < mList!!.size) {
                    mList!!.removeAt(position)
                    if (useHaveAnimationRefresh) {
                        notifyItemRemoved(position + headerCount)
                        notifyItemRangeChanged(
                            position + headerCount, itemCount - position - headerCount
                        )
                    } else notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param position
     */
    fun remove(data: T) {
        try {
            val position = list.indexOf(data)
            if (mList != null) {
                if (position > -1 && position < mList!!.size) {
                    mList!!.removeAt(position)
                    if (useHaveAnimationRefresh) {
                        notifyItemRemoved(position + headerCount)
                        notifyItemRangeChanged(
                            position + headerCount, itemCount - position - headerCount
                        )
                    } else notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun removeAll() {
        if (mList != null) {
            mList!!.clear()
            notifyDataSetChanged()
        }
    }
}
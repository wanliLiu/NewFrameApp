package com.soli.libcommon.base

import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter
import java.util.*

/**
 * @author Soli
 * @Time 2018/11/27 14:32
 */
abstract class BaseListAdapter<T> : BaseAdapter {

    protected var mList: MutableList<T>? = null

    protected var ctx: Context

    protected var mInflater: LayoutInflater


    constructor(context: Context) {
        this.ctx = context
        mInflater = LayoutInflater.from(ctx)
    }

    constructor(context: Context, list: MutableList<T>?) {
        this.ctx = context
        this.mList = list
        mInflater = LayoutInflater.from(ctx)
    }


    fun setList(list: MutableList<T>?) {
        mList = ArrayList()
        this.mList = list
        notifyDataSetChanged()
    }

    fun getList(): MutableList<T> {
        if (mList == null) {
            mList = ArrayList()
        }
        return mList!!
    }


    override fun getCount(): Int {
        return mList?.size ?: 0
    }

    override fun getItem(position: Int): T? {
        return if (mList != null && mList!!.size > 0 && position < count) mList!![position] else null
    }

    override fun getItemId(position: Int): Long {
        return (if (mList == null) 0 else position).toLong()
    }


    fun add(t: T) {
        if (mList == null) {
            mList = ArrayList()
        }
        mList!!.add(t)
        notifyDataSetChanged()
    }

    operator fun set(location: Int, t: T) {
        if (mList == null) {
            mList = ArrayList()
        }
        mList!![location] = t
        notifyDataSetChanged()
    }

    fun add(location: Int, t: T) {
        if (mList == null) {
            mList = ArrayList()
        }
        mList!!.add(location, t)
        notifyDataSetChanged()
    }

    fun addAll(list: List<T>?) {
        if (mList == null) {
            mList = ArrayList()
        }
        if (list != null) {
            mList!!.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun addAllToFirst(list: List<T>) {
        if (mList == null) {
            mList = ArrayList()
        }
        mList!!.addAll(0, list)
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        if (mList != null) {
            mList!!.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun removeListData(position: Int) {
        if (mList != null) {
            mList!!.removeAt(position)
        }
    }

    fun removeAll() {
        if (mList != null) {
            mList!!.clear()
            notifyDataSetChanged()
        }
    }

    fun removeAll(list: List<T>) {
        if (mList != null) {
            mList!!.removeAll(list)
            notifyDataSetChanged()
        }
    }
}

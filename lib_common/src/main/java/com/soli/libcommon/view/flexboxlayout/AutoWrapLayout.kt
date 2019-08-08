package com.soli.libcommon.view.flexboxlayout

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import com.google.android.flexbox.*

/**
 *
 * @author Soli
 * @Time 2018/11/27 14:06
 */
class AutoWrapLayout(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FlexboxLayout(ctx, attrs, defStyleAttr) {


    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    private var mAdapter: AutoWrapAdapter<*>? = null

    init {

        //一块布局是内容从哪里开始
        alignContent = AlignContent.FLEX_START

        alignItems = AlignItems.FLEX_START

        //行排列
        flexDirection = FlexDirection.ROW

        //可多行
        flexWrap = FlexWrap.WRAP

        //内容行，从左到右依次排列
        justifyContent = JustifyContent.FLEX_START

        setShowDivider(SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_END or SHOW_DIVIDER_MIDDLE)
    }


    /**
     * 设置内容居中对齐  默认是从左到右依次排列
     */
    fun setContentCenter() {
        justifyContent = JustifyContent.CENTER
    }

    /**
     * 设置数据 从新移除数据，重新添加视图
     */
    fun <T> setAdapter(adapter: AutoWrapAdapter<T>) {
        mAdapter = adapter
        mAdapter!!.notifyCustomListView(this)
    }

    /**
     * @param listener
     */
    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        mAdapter!!.setOnItemClickListener(listener)
    }

    /**
     * Corresponding Item long click event
     *
     * @param listener
     */
    fun setOnItemLongClickListener(listener: AdapterView.OnItemLongClickListener) {
        mAdapter!!.setOnItemLongClickListener(listener)
    }
}
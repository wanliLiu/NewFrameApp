package com.soli.libcommon.bottomsheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 *
 * @author Soli
 * @Time 2018/11/12 15:15
 */
abstract class BaseSheetFragment : BottomSheetDialogFragment() {

    /**
     * 上下文context
     */
    protected var ctx: Context? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        ctx = context
        return BottomDialog(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContentView(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view, savedInstanceState)
        initListener()
        initData()
    }

    protected fun getSheetDialog(): BottomDialog = dialog as BottomDialog

    /**
     * 获取内容视图
     */
    protected abstract fun getContentView(): Int

    protected abstract fun initView(view: View, savedInstanceState: Bundle?)
    protected abstract fun initListener()
    protected abstract fun initData()
    /**
     *默认高度是到导航栏下面
     */
    protected fun topOffsetDefault() {
        getSheetDialog().topOffsetDefault()
    }

    /**
     * 设置到顶部的偏移量
     */
    protected fun setTopOffset(offset: Int) {
        getSheetDialog().setTopOffset(offset)
    }

}
package com.soli.lib_common.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soli.lib_common.R
import com.soli.lib_common.view.root.LoadingType
import com.soli.lib_common.view.root.RootView

/**
 * @author Soli
 * @Time 18-5-16 上午11:10
 */
open abstract class BaseFragment : BaseFunctionFragment() {

    protected var defaultLoadingType = LoadingType.TypeInside
    private var loadingType = defaultLoadingType
    protected lateinit var rootView: RootView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_root_view, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContentViews(view)
        initView()
        initListener()
        initData()
    }

    /**
     *
     */
    private fun setContentViews(view: View) {
        rootView = RootView(ctx as Activity, view, getContentView(), true)
    }

    /**
     * 获取内容视图
     */
    protected abstract fun getContentView(): Int

    protected abstract fun initView()
    protected abstract fun initListener()
    protected abstract fun initData()

    override fun showProgress(show: Boolean) {
        if (show) showProgress()
    }

    override fun showProgress(type: LoadingType) {
        loadingType = type
        showProgress()
    }

    override fun showProgress() {
        when (loadingType) {
            LoadingType.TypeDialog -> showProgressDialog()
            LoadingType.TypeInside -> rootView.showProgressInside(getProgressView())
        }
    }

    override fun dismissProgress() {
        rootView.dissShowProgressInside()
        dissProgressDialog()
        //恢复到默认值
        if (loadingType !== defaultLoadingType) {
            loadingType = defaultLoadingType
        }
    }

    /**
     * 获取视图加载
     *
     * @return
     */
    open fun getProgressView(): Int {
        return R.layout.loding_inside
    }

}
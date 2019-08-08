package com.soli.libcommon.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soli.libcommon.R
import com.soli.libcommon.view.root.LoadingType
import com.soli.libcommon.view.root.RootView

/**
 * @author Soli
 * @Time 18-5-16 上午11:10
 */
abstract class BaseFragment : BaseFunctionFragment() {

    protected var defaultLoadingType = LoadingType.TypeInside
    private var loadingType = defaultLoadingType
    protected lateinit var rootView: RootView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_root_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContentViews(view)
        initView()
        initListener()
        initData()
    }

    /**
     * Fragment中是否需要toolbar，默认不需要
     */
    open fun needTopToolbar() = false

    /**
     *
     */
    open fun setTitle(title: CharSequence) {
        rootView.setTitle(title)
    }

    /**
     *
     */
    open fun setTitle(titleId: Int) {
        rootView.setTitle(titleId)
    }

    /**
     *
     */
    private fun setContentViews(view: View) {
        rootView = RootView(ctx as Activity, view, getContentView(), needTopToolbar())
    }

    /**
     *
     */
    fun addIconMenu(idIndex: Int, resId: Int) {
        rootView.getToolbar()?.addIconMenu(idIndex, resId)
    }

    fun addTextMenu(idIndex: Int, text: String?, colorId: Int) {
        rootView.getToolbar()?.addTextMenu(idIndex, text, colorId)
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
            else -> {
            }
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
        return R.layout.loding_inside_top
    }

    /**
     * tab 切换
     */
    open fun Resume() {
    }

    /**
     *
     */
    fun errorHappen(listener: () -> Unit) {
        rootView.errorHappen(listener, R.layout.error_trouble_layout, R.id.btnRetry)
//        TODO("可以根据情况实际做相应的调整，这里只是case")
    }

    /**
     * 没有视图的时候显示的视图,然后这里是需要手动添加
     */
    fun hasNoResult(layoutId: Int = R.layout.has_no_content_layout) {
        rootView.errorHappen({}, layoutId, null)
    }
}
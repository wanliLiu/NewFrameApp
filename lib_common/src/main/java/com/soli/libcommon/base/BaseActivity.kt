package com.soli.libcommon.base

import android.os.Bundle
import com.soli.libcommon.R
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.util.ToastUtils
import com.soli.libcommon.view.root.LoadingType
import com.soli.libcommon.view.root.RootView

/**
 * @author Soli
 * @Time 18-5-15 下午3:25
 */
abstract class BaseActivity : BaseFunctionActivity() {

    open var defaultLoadingType = LoadingType.TypeInside
    private var loadingType = defaultLoadingType
    protected lateinit var rootView: RootView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentViews()
        initView()
        initListener()
        initData()
    }

    /**
     * Activity默认需要顶部的toolbar
     */
    open fun needTopToolbar() = true

    /**
     *
     */
    private fun setContentViews() {
        setContentView(R.layout.activity_root_view)
        rootView = RootView(this, getContentView(), needTopToolbar())
        setStatusBarColor()
    }

    /**
     * 获取内容视图
     */
    protected abstract fun getContentView(): Int

    protected abstract fun initView()
    protected abstract fun initListener()
    protected abstract fun initData()

    override fun showProgress(show: Boolean, cancle: Boolean, type: LoadingType) {

        if (!show) return

        if (isFinishing)
            return

        loadingType = type

        when (loadingType) {
            LoadingType.TypeDialog -> showProgressDialog(cancle)
            LoadingType.TypeInside -> rootView.showProgressInside(getProgressView())
            else -> {
            }
        }
    }

    override fun dismissProgress() {
        rootView.dissShowProgressInside()
        dissProgressDialog()
        //恢复到默认值
        if (loadingType !== LoadingType.TypeInside) {
            loadingType = LoadingType.TypeInside
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
     * 类似在里面的加载效果
     */
    open fun getProgressLikeDialog(): Int {
        return R.layout.loding_inside_dialog
    }


    override fun setTitle(title: CharSequence) {
        rootView.setTitle(title)
    }

    override fun setTitle(titleId: Int) {
        rootView.setTitle(titleId)
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
     *
     */
    fun errorHappen(pageNo: Int = 1, result: ApiResult<Any>, listener: () -> Unit) {
        if (pageNo == 1)
            rootView.errorHappen(listener, R.layout.error_trouble_layout, R.id.btnRetry)
        else
            ToastUtils.showShortToast(result.errormsg)
    }

    /**
     * 没有视图的时候显示的视图,然后这里是需要手动添加
     */
    fun hasNoResult(layoutId: Int = R.layout.has_no_content_layout) {
        rootView.errorHappen({}, layoutId, null)
    }
}
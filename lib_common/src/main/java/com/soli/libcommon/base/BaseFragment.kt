package com.soli.libcommon.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soli.libcommon.R
import com.soli.libcommon.net.ApiResult
import com.soli.libcommon.util.ToastUtils
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    open fun setContentViews(view: View) {
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

    override fun showProgress(show: Boolean, cancle: Boolean, type: LoadingType) {

        if (!show) return

        loadingType = type

        when (loadingType) {
            LoadingType.TypeDialog -> showProgressDialog(cancle)
            LoadingType.TypeInside -> rootView.showProgressInside(getProgressView())
            else -> {
            }
        }
    }

    override fun dismissProgress() {
        try {
            rootView.dissShowProgressInside()
            dissProgressDialog()
            //恢复到默认值
            if (loadingType !== LoadingType.TypeInside) {
                loadingType = LoadingType.TypeInside
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
     *
     */
    fun <T> errorHappen(pageNo: Int = 1, result: ApiResult<T>, listener: () -> Unit) {
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
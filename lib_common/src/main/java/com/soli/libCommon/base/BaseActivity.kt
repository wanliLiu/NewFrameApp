package com.soli.libCommon.base

import android.os.Bundle
import com.soli.libCommon.view.root.LoadingType
import com.soli.libCommon.view.root.RootView

/**
 * @author Soli
 * @Time 18-5-15 下午3:25
 */
abstract class BaseActivity : BaseFunctionActivity() {

    protected var defaultLoadingType = LoadingType.TypeInside
    private var loadingType = defaultLoadingType
    protected lateinit var rootView: RootView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentViews()
        initView()
        initDefaultBack()
        initListener()
        initData()
    }

    /**
     *
     */
    private fun initDefaultBack() {
        //是否要显示返回的icon
        if (needShowBackIcon()) {
            //默认显示，点击关闭
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            rootView.getToolbar()?.setNavigationOnClickListener { onBackPressed() }
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }
    }

    /**
     * 是否要显示返回的按钮
     *
     * @return 默认要显示
     */
    open fun needShowBackIcon(): Boolean {
        return true
    }

    /**
     *
     */
    private fun setContentViews() {
        setContentView(R.layout.activity_root_view)
        rootView = RootView(this, getContentView())

        setSupportActionBar(rootView.getToolbar())
        supportActionBar?.title = null
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
            LoadingType.TypeDialog -> showProgressDialog() //rootView.showProgressInside(getProgressLikeDialog()) showProgressDialog()
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
        return R.layout.loding_inside
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
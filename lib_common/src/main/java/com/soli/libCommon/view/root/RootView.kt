package com.soli.libCommon.view.root

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.soli.libCommon.R
import com.soli.libCommon.util.StatusBarUtil

/**
 * @author Soli
 * @Time 2017/8/8
 */
class RootView(mctx: Activity, rooContent: View, showView: Int, isNeedToolbar: Boolean = false) {
    //内容根视图
    //视图窗根视图
    private var rootView: ViewGroup

    private var content: ViewGroup? = null
    private var toolbar: Toolbar? = null
    private var justyContent: ViewGroup? = null

    //显示的视图即，getContentView视图
    private var viewDisplay: View? = null
    private var errorview: View? = null
    private var progressView: View? = null

    private var mInflater: LayoutInflater
    private var ctx: Context = mctx

    init {
        mInflater = LayoutInflater.from(ctx)
        rootView = rooContent as ViewGroup
        initView(showView, isNeedToolbar)
    }

    /**
     * @param mctx
     * @param showView
     * @param isNeedToolbar
     */
    constructor(mctx: Activity, showView: Int, isNeedToolbar: Boolean = false) : this(
        mctx,
        mctx.findViewById<View>(R.id.viewRoot) as ViewGroup,
        showView,
        isNeedToolbar
    )

    /**
     * 调整进度加载的视图 的容器
     */
    fun justyProgressAreaContentTo(view: ViewGroup) {
        justyContent = view
        viewDisplay = if (justyContent!!.childCount > 0) justyContent!!.getChildAt(0) else null
    }

    /**
     * 重新定位toolbar 和 content
     */
    fun reInitView() {
        rootView.apply {
            content = findViewById(R.id.root_content)
            toolbar = findViewById(R.id.tool_bar)
        }
    }

    /**
     *
     */
    private fun initView(showView: Int, isNeedToolbar: Boolean) {
        if (!isNeedToolbar) {
            rootView.removeAllViews()
            content = rootView
        } else {
            reInitView()
        }

        //add content
        viewDisplay = setContentView(showView)
    }


    /**
     * 把root_content 调整到屏幕顶端，前提是内容全屏SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     */
    fun offsetContentToScreenTop() {
        offsetContent(0)
    }

    /**
     *
     */
    private fun offsetContent(offset: Int) {
        content?.apply {
            val params = layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = offset
            layoutParams = params
        }
    }

    /**
     * 调整toolbar到屏幕顶端的位置，前提是内容全屏SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     */
    fun judgeToolBarOffset() {
        toolbar?.apply {
            val statbarHeight = StatusBarUtil.getStatusBarHeight(ctx)
            getBarRoot().setPadding(0, statbarHeight, 0, 0)
            offsetContent(statbarHeight + ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height))
        }
    }

    /**
     * 设置导航栏的原色
     */
    fun setToolbarBackgroudColor(color: Int) {
        toolbar?.setToolBackgroundColor(color)
    }

    /**
     * @param view
     */
    private fun removeView(view: View?) {
        if (justyContent != null && view != viewDisplay) {
            justyContent!!.removeView(view)
        } else
            content?.removeView(view)
    }

    /**
     * @param view
     */
    private fun setContentView(view: View?): View? {
        if (justyContent != null && view != viewDisplay) {
            justyContent!!.addView(view)
        } else
            content?.addView(view)
        return view
    }

    /**
     * 设置主要显示视图
     *
     * @param resoucreId
     */
    private fun setContentView(resoucreId: Int): View? {
        return setContentView(mInflater.inflate(resoucreId, null))
    }

    /**
     * 添加有相应处理事件视图
     *
     * @param listener 操作回调
     * @param layout   显示的视图
     * @param id       需要操作的资源id
     */
    private fun setContentView(listener: () -> Unit, layout: Int, vararg id: Int?): View {
        val view = mInflater.inflate(layout, null)

        for (index in id) {
            index?.let {
                view.findViewById<View>(it).setOnClickListener { listener.invoke() }
            }
        }

        return view
    }

    /**
     * 设置标题栏
     *
     * @param title
     */
    fun setTitle(title: Any) {
        if (title is Int) {
            toolbar?.setTitle(ctx.resources.getString(title))
        } else {
            toolbar?.setTitle(title as String)
        }
    }

    /**
     *
     */
    fun setTitleLeft(title: Any) {
        if (title is Int) {
            toolbar?.setTitleLeft(ctx.resources.getString(title))
        } else {
            toolbar?.setTitleLeft(title as String)
        }
    }

    /**
     * 第一次加载的时候出现加载动画框
     */
    fun showProgressInside(layout: Int) {
        removeView(errorview)
        removeView(progressView)
        progressView = null
        errorview = progressView

        viewDisplay?.visibility = View.INVISIBLE

        progressView = setContentView(layout)
    }

    /**
     * 错误发生了
     *
     * @param listener
     * @param layout
     * @param id
     */
    fun errorHappen(listener: () -> Unit, layout: Int, vararg id: Int?) {
        removeView(errorview)
        errorview = null

        viewDisplay?.visibility = View.INVISIBLE

        errorview = setContentView(listener, layout, *id)

        if (progressView == null)
            setContentView(errorview)
        else
            viewAnimation(errorview, progressView)
    }

    /**
     *
     */
    fun dissShowProgressInside() {
        viewAnimation(viewDisplay, errorview)
        viewAnimation(viewDisplay, progressView)
    }

    /**
     * @param showView
     * @param dissMissView
     */
    private fun viewAnimation(showView: View?, dissMissView: View?) {
        if (dissMissView == null) return

        showView?.apply {
            if (this === viewDisplay && visibility != View.VISIBLE)
                visibility = View.VISIBLE
        }


        if (viewDisplay != null && showView != viewDisplay)
            setContentView(showView)

        removeView(dissMissView)
        setContentView(dissMissView)

        val set = AnimatorSet()
        set.interpolator = AccelerateDecelerateInterpolator()
        set.playTogether(
            ObjectAnimator.ofFloat(showView, "alpha", 0.0f, 1.0f),
            ObjectAnimator.ofFloat(dissMissView, "alpha", 1f, 0.0f)
        )
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                removeView(dissMissView)
                if (dissMissView === errorview) {
                    errorview = null
                }
                if (dissMissView === progressView) {
                    progressView = null
                }
            }
        })
        set.setDuration(300).start()
    }

    /**
     * 整个setcontentView的视图
     *
     * @return
     */
    fun getContentView() = content

    fun getRootView() = rootView

    /**
     *
     */
    fun getToolbar() = toolbar
}

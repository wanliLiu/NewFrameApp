package com.soli.lib_common.view.root

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.soli.lib_common.R

/**
 * @author Soli
 * @Time 2017/8/8
 */
class RootView(mctx: Activity, rooContent: View, showView: Int, isClear: Boolean = false) {
    //内容根视图
    //视图窗根视图
    private var contentView: ViewGroup

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
        contentView = rooContent as ViewGroup
        initView(showView, isClear)
    }

    /**
     * @param mctx
     * @param showView
     * @param isClear
     */
    constructor(mctx: Activity, showView: Int, isClear: Boolean = false) : this(mctx, mctx.findViewById<View>(R.id.viewRoot) as ViewGroup, showView, isClear)

    /**
     * 调整进度加载的视图 的容器
     */
    fun justyProgressAreaContentTo(view: ViewGroup) {
        justyContent = view
        viewDisplay = if (justyContent!!.childCount > 0) justyContent!!.getChildAt(0) else null
    }

    /**
     *
     */
    private fun initView(showView: Int, isClear: Boolean) {
        if (isClear) {
            contentView.removeAllViews()
            content = contentView
        } else {
            contentView.apply {
                content = findViewById(R.id.root_content)
                toolbar = findViewById(R.id.tool_bar)
            }
        }

        //add content
        viewDisplay = setContentView(showView)
    }

    /**
     * 隐藏顶部视图
     */
    fun hideToolBar() {
        toolbar?.visibility = View.GONE
        offsetContentToStatusBar()
    }

    /**
     *
     */
    private fun offsetContentToStatusBar() {
        content?.apply {
            val params = layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = 0
            layoutParams = params
        }
    }

    /**
     * @param view
     */
    private fun removeView(view: View?) {
        if (justyContent != null && view != viewDisplay) {
            justyContent!!.removeView(view)
        } else
            content!!.removeView(view)
    }

    /**
     * @param view
     */
    private fun setContentView(view: View?): View? {
        if (justyContent != null && view != viewDisplay) {
            justyContent!!.addView(view)
        } else
            content!!.addView(view)
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
            toolbar?.title = ctx.resources.getString(title)
        } else {
            toolbar?.title = title as CharSequence
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
        set.playTogether(ObjectAnimator.ofFloat(showView, "alpha", 0.0f, 1.0f), ObjectAnimator.ofFloat(dissMissView, "alpha", 1f, 0.0f))
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
    fun getContentView() = contentView

    /**
     *
     */
    fun getToolbar() = toolbar
}

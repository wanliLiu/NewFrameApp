package com.soli.libCommon.view.root

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.soli.libCommon.R
import com.soli.libCommon.util.MLog
import kotlinx.android.synthetic.main.toolbar_layout.view.*


/**
 * @author Soli
 * @Time 2018/10/11 10:19
 */
@SuppressLint("CustomViewStyleable")
class Toolbar(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var ctx = context
    private var back_layout: RelativeLayout
    private var mTitle: TextView
    private var tv_commit: TextView
    private var mRightBtn: ImageButton
    private var mRightBtnLeft: ImageButton
    private var loadprogress: ProgressBar

    private var title: String? = null
    private var commitText: String? = null
    private var commitColor: Int = 0
    private var right_image_res: Int = 0


    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)


    init {

        title = ""
        commitText = ""
        LayoutInflater.from(ctx).inflate(R.layout.toolbar_layout, this)
        loadprogress = findViewById(R.id.loadprogress)
        back_layout = findViewById(R.id.back_layout)
        mTitle = findViewById(R.id.tv_title)
        tv_commit = findViewById(R.id.tv_commit)
        mRightBtnLeft = findViewById(R.id.iv_right_btn_left)
        mRightBtn = findViewById(R.id.iv_right_btn)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.toolbar_attr)
            title = a.getString(R.styleable.toolbar_attr_setTitle)
            commitText = a.getString(R.styleable.toolbar_attr_commitText)
            commitColor = a.getColor(R.styleable.toolbar_attr_commitTextColor, 0)
            right_image_res = a.getResourceId(R.styleable.toolbar_attr_right_image, 0)
            a.recycle()
        }

        setTitle(title)
        setRightText(commitText)

        if (commitColor != 0)
            setRightTextColor(commitColor)

        if (right_image_res != 0) {
            mRightBtn.setImageResource(right_image_res)
        }

        back_layout.setOnClickListener {
            (context as Activity).onBackPressed()
        }
    }

    /**
     *
     */
    fun hideBackFunction() {
        tv_back.visibility = View.GONE
        back_layout.isEnabled = false
    }

    /**
     * 设置标题
     *
     * @param title
     */
    fun setTitle(title: String?) {
        mTitle.text = title ?: ""
    }

    /**
     * 设置右部字体
     *
     * @param title
     * @param color
     */
    fun setRightText(title: String, color: Int) {
        setRightText(title)
        setRightTextColor(color)
    }

    /**
     * 设置右部字体
     *
     * @param title
     */
    fun setRightText(title: String?) {
        tv_commit.text = title ?: ""
        tv_commit.visibility = if (!TextUtils.isEmpty(title)) View.VISIBLE else View.GONE
    }

    /**
     * 设置右部字体颜色
     *
     * @param color
     */
    fun setRightTextColor(color: Int) {
        tv_commit.setTextColor(color)
    }

    /**
     * @param clickListener
     */
    fun setRightTextClickListener(clickListener: View.OnClickListener) {
        tv_commit.setOnClickListener(clickListener)
        tv_commit.visibility = View.VISIBLE
    }

    /**
     * @param resId
     */
    fun setRightImageResourcer(@DrawableRes resId: Int) {
        mRightBtn.setImageResource(resId)
    }

    /**
     * @return
     */
    fun getmRightBtnLeft(): ImageButton {
        return mRightBtnLeft
    }

    fun getRightBtn(): ImageButton {
        return mRightBtn
    }

    fun setRightImageVisible(visibility: Int) {
        mRightBtn.visibility = visibility
    }

    fun setOnRightImageClickListener(listener: View.OnClickListener) {
        mRightBtn.setOnClickListener(listener)
        mRightBtn.visibility = View.VISIBLE
    }

    fun setLeftTextClickListener(clickListener: View.OnClickListener) {
        back_layout.setOnClickListener(clickListener)
    }


    /**
     * 设置顶部的加载进度条
     *
     * @param loading
     */
    fun setLoadingProgress(loading: Int) {
        MLog.e("loading", loading.toString() + "")
        loadprogress.progress = if (loading > 0) loading else 3
    }

    /**
     * @param isShow
     */
    fun showLoadingProgress(isShow: Boolean) {
        loadprogress.visibility = if (isShow) View.VISIBLE else View.GONE
        //        findViewById(R.id.barLine).setVisibility(isShow ? INVISIBLE : VISIBLE);
    }
}

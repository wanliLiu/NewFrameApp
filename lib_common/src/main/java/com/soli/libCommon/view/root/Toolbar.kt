package com.soli.libCommon.view.root

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.soli.libCommon.R
import com.soli.libCommon.util.MLog
import com.soli.libCommon.util.ViewUtil


/**
 * @author Soli
 * @Time 2018/10/11 10:19
 */
@SuppressLint("CustomViewStyleable")
class Toolbar(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    private var ctx = context

    private val barBackIcon: ImageView
    private val barTitle: TextView
    private val barRoot: FrameLayout
    private val loadprogress: ProgressBar

    private var onBarMenuClick: onBarMenuClickListener? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.toolbar_layout, this)

        barBackIcon = findViewById(R.id.barBackIcon)
        barTitle = findViewById(R.id.barTitle)
        barRoot = findViewById(R.id.barRoot)
        loadprogress = findViewById(R.id.loadprogress)

        barBackIcon.setOnClickListener {
            (context as Activity).onBackPressed()
        }
    }

    private fun dpto(dp: Float) = ViewUtil.dip2px(dp, ctx)

    /**
     * 设置返回按钮的资源，比如关闭
     */
    fun setBackResource(resId: Int) {
        barBackIcon.setImageResource(resId)
    }

    /**
     *默认是设置中间的标题
     */
    fun setTitle(title: String) {
        barTitle.text = title
    }

    /**
     *设置左边的标题
     */
    fun setTitleLeft(title: String) {
        setTitle(title)
        barBackIcon.visibility = View.GONE
        (barTitle.layoutParams as FrameLayout.LayoutParams).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            leftMargin = dpto(15f)
            barTitle.layoutParams = this
        }
        barTitle.paint.isFakeBoldText = true
        barTitle.textSize = 23f
    }

    private fun getAddMenuParams(isIcon: Boolean = false): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            if (isIcon) ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height) else LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    /**
     *
     */
    private fun getMenuContainer(): LinearLayout {
        return findViewById(R.id.id_toolbar_custom) ?: LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            id = R.id.id_toolbar_custom

            val params =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height)
                )
                    .apply {
                        gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                    }

            barRoot.addView(this, params)
        }
    }

    /**
     * 添加图标按钮
     */
    private fun getImageView(idIndex: Int, resId: Int):ImageView {
        return ImageView(ctx).apply {
            id = idIndex
            val _dp = dpto(15f)
            setPadding(dpto(10f), _dp, _dp, _dp)
            setImageResource(resId)
            setBackgroundResource(R.drawable.default_view_press_selector)
            setOnClickListener {
                onBarMenuClick?.onBarMenuClick(id, this)
            }
        }
    }

    /**
     * 添加文字按钮
     */
    private fun getTextView(idIndex: Int, str: String?, colorId: Int): TextView {
        return TextView(ctx).apply {
            id = idIndex
            setPadding(dpto(10f), 0, dpto(15f), 0)
            text = str ?: ""

            gravity = Gravity.CENTER

            setTextColor(context.resources.getColor(colorId))

            setBackgroundResource(R.drawable.default_view_press_selector)

            setOnClickListener {
                onBarMenuClick?.onBarMenuClick(id, this)
            }
        }
    }

    /**
     *
     */
    fun setCustomView(layoutId: Int) = {
        val view = LayoutInflater.from(ctx).inflate(layoutId, null)
        setCustomView(view)
        view
    }

    /**
     * 自定义toolbar的内容
     */
    fun setCustomView(view: View) {
        barRoot.removeAllViews()
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            ctx.resources.getDimensionPixelOffset(R.dimen.toolbar_height)
        )
        barRoot.addView(view, params)
    }

    /**
     *
     */
    fun setOnBarMenuClicklistener(listener: onBarMenuClickListener?) {
        onBarMenuClick = listener
    }

    /**
     *
     */
    fun addIconMenu(idIndex: Int, resId: Int) {
        val container = getMenuContainer()
        container.addView(getImageView(idIndex, resId), -1, getAddMenuParams(true))
    }

    /**
     *
     */
    fun getIconMenu(idIndex: Int):ImageView = getMenuContainer().findViewById(idIndex)

    /**
     *
     */
    fun addTextMenu(idIndex: Int, text: String?, colorId: Int) {
        val container = getMenuContainer()
        container.addView(getTextView(idIndex, text, colorId), -1, getAddMenuParams())
    }

    /**
     *
     */
    fun getTextMenu(idIndex: Int): TextView = getMenuContainer().findViewById(idIndex)

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
     * 设置toolbar背景色
     */
    fun setToolBackgroundColor(color: Int) {
        barRoot.setBackgroundColor(color)
    }

    /**
     * @param isShow
     */
    fun showLoadingProgress(isShow: Boolean) {
        loadprogress.visibility = if (isShow) View.VISIBLE else View.GONE
        //        findViewById(R.id.barLine).setVisibility(isShow ? INVISIBLE : VISIBLE);
    }

    fun getBarRoot() = barRoot


}

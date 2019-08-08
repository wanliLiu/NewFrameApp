package com.soli.newframeapp.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.soli.newframeapp.R

/**
 *
 * @author Soli
 * @Time 2019-08-06 16:12
 */
class SpecialTextView(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TextView(context, attrs, defStyleAttr), View.OnTouchListener {

    private var isNeedPressStatus = true
    private var isHavePressed = false
    private var lastClickSpan: ClickableSpan? = null

    private var useMoveMethod = true

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)


    private fun initView() {
        if (useMoveMethod) {
            setBackgroundResource(R.drawable.selector_transparent)
//            movementMethod = SpecialMoveMethod.getInstance()
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.parseColor("#806CA5FF")
            setOnLongClickListener { true }
        } else {
            setOnTouchListener(this)
        }
    }

    /**
     *
     */
    fun anotherTest() {

        initView()

//        setText("")
        text = ""
        append(getSpecialSpan("@来看我啊"))
        append(getSpecialSpan("@不是吧0 "))
        append(getSpecialSpan("@不是吧1 "))
        append(getSpecialSpan("@不是吧2 "))
        append(getSpecialSpan("@不是吧3 "))
        append(getSpecialSpan("@不是吧4 "))
        append(getSpecialSpan("@不是吧5 "))
        append(getSpecialSpan("@不是吧6 "))
        append(getSpecialSpan("@不是吧7 "))

        testPic()
        testPic()
        testPic()
        testPic()
        testPic()
        testPic()
    }

    /**
     *
     */
    private fun testPic() {

//        initView()
//        setText("我的上看到楼上的看老师的看")
//        text = "我的上看到楼上的看老师的看"
        append("我的上看到楼上的看老师的看")
        val str = "    点击图片"
        val sp = SpannableStringBuilder()
        val spanString = SpannableString(str)
        spanString.setSpan(clickSpan(), 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ContextCompat.getDrawable(context, R.mipmap.icon_see_image)?.apply {
            val pannding = (context.resources.displayMetrics.density * 1 + 0.5f).toInt()
            setBounds(0, 0 - pannding, intrinsicWidth - pannding, intrinsicHeight - pannding)
            spanString.setSpan(CustomImageSpan(this), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        sp.append(spanString)
        sp.append("的楼上的楼上的是代理商；的数量； 登录熟练度")

        sp.append(getSpecialSpan("@来看我啊"))
        sp.append(getSpecialSpan("@不是吧"))
//        append(spanString)
//        append("的楼上的楼上的是代理商；的数量； 登录熟练度")

        append(sp)
    }


    private fun getSpecialSpan(text: String): SpannableString {
        return SpannableString(text).apply {
            setSpan(clickSpan(text), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v ?: return false
        event ?: return false

        val text = (v as TextView).text
        val buffer = SpannableString.valueOf(text)
        val action = event.action

        var x = event.x.toInt()
        var y = event.y.toInt()

        x -= v.totalPaddingLeft
        y -= v.totalPaddingTop

        x += v.scrollX
        y += v.scrollY

        val layout = v.layout
        val line = layout.getLineForVertical(y)
        val off = layout.getOffsetForHorizontal(line, x.toFloat())

        val link = buffer.getSpans(off, off, ClickableSpan::class.java)
        if (link.isNotEmpty()) {
            val inSelect = link[0]
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    lastClickSpan = inSelect
                    setClickBackgoundColorSpan(buffer, inSelect)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (lastClickSpan != inSelect) {
                        removeClickStatusSpan(buffer)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (lastClickSpan == inSelect) {
                        inSelect.onClick(v)
                    }
                    lastClickSpan = null
                    removeClickStatusSpan(buffer)
                }
            }
            return true
        } else {
            removeClickStatusSpan(buffer)
        }

        return false
    }


    /**
     * @param buffer
     * @param start
     * @param end
     */
    private fun setClickBackgoundColorSpan(buffer: Spannable, mclickSpan: ClickableSpan) {
        if (!isNeedPressStatus) return

        if (mclickSpan is clickSpan)
            mclickSpan.isPressed = true

//
//        buffer.setSpan(
//            ClickBackgroundColorSpan(),
//            buffer.getSpanStart(mclickSpan),
//            buffer.getSpanEnd(mclickSpan),
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        text = buffer
        isHavePressed = true
    }

    /**
     * @param buffer
     */
    private fun removeClickStatusSpan(buffer: Spannable) {

        if (!isNeedPressStatus) return

        if (isHavePressed) {
            isHavePressed = false
            val list = buffer.getSpans(0, buffer.length, clickSpan::class.java)
            if (list.isNotEmpty()) {
                list.forEach {
//                    buffer.removeSpan(it)
                    it.isPressed = false
                }
                text = buffer
            }
        }
    }

    /**
     * 点击后的背景原色span
     */
    private class ClickBackgroundColorSpan : BackgroundColorSpan(Color.parseColor("#806CA5FF"))

    /**
     *
     */
    private class clickSpan(val text: String = "默认数据") : ClickableSpan() {

        var isPressed = false

        override fun onClick(view: View) {
            Toast.makeText(view.context, text, Toast.LENGTH_SHORT).show()
        }


        override fun updateDrawState(ds: TextPaint) {
//            super.updateDrawState(ds)
//            ds.isFakeBoldText = true
//            ds.bgColor = if (isPressed) Color.parseColor("#806CA5FF") else Color.TRANSPARENT
            ds.color = Color.parseColor("#6CA5FF")
            ds.isUnderlineText = false
        }
    }

    class CustomImageSpan(d: Drawable) : ImageSpan(d) {
        /**
         * 让图片居中 start
         */
        override fun draw(
            canvas: Canvas, text: CharSequence, start: Int, end: Int,
            x: Float, top: Int, y: Int, bottom: Int, paint: Paint
        ) {
            val b = drawable
            // font metrics of text to be replaced
            val fm = paint.fontMetricsInt
            val transY = (y + fm.descent + y + fm.ascent) / 2 - b.bounds.bottom / 2
            canvas.save()
            canvas.translate(x, transY.toFloat())
            b.draw(canvas)
            canvas.restore()
        }
    }


    class SpecialMoveMethod : LinkMovementMethod() {

        companion object {

            private var sInstance: SpecialMoveMethod? = null

            fun getInstance(): SpecialMoveMethod {

                if (sInstance == null)
                    sInstance = SpecialMoveMethod()
                return sInstance!!

            }
        }

//        override fun handleMovementKey(
//            widget: TextView?,
//            buffer: Spannable?,
//            keyCode: Int,
//            movementMetaState: Int,
//            event: KeyEvent?
//        ): Boolean {
//            return false
//        }

//        override fun onTouchEvent(v: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
//            v ?: return false
//            event ?: return false
//            buffer ?: return false
//
//            val action = event.action
//
//            var x = event.x.toInt()
//            var y = event.y.toInt()
//
//            x -= v.totalPaddingLeft
//            y -= v.totalPaddingTop
//
//            x += v.scrollX
//            y += v.scrollY
//
//            val layout = v.layout
//            val line = layout.getLineForVertical(y)
//            val off = layout.getOffsetForHorizontal(line, x.toFloat())
//
//            val link = buffer.getSpans(off, off, ClickableSpan::class.java)
//            if (link.isNotEmpty()) {
//                if (action == MotionEvent.ACTION_UP) {
//                    link[0].onClick(v)
//                }
//                return true
//            }
//            return false
//        }
    }
}
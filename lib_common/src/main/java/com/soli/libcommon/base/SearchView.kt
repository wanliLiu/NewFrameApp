package com.soli.libcommon.base

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.soli.libcommon.R
import com.soli.libcommon.util.KeyBoardUtils
import com.soli.libcommon.util.clickView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import me.yokeyword.fragmentation.SupportActivity
import java.util.concurrent.TimeUnit

/**
 *  Toolbar顶部搜索视图
 * @author Soli
 * @Time 2018/11/23 12:45
 */
class SearchView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    constructor(ctx: Context) : this(ctx, null, 0)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    private var ctx = context

    private val searchClear: View
    private val searchFinish: View
    private val searchInput: EditText

    private var textWatcher: TextWatcher? = null

    private var isFromManuSet = false
    var needNotifyWhenAutoAtManuSet = true

    init {
        LayoutInflater.from(ctx).inflate(R.layout.toolbar_search_layout, this)

        searchClear = findViewById(R.id.searchClear)
        searchFinish = findViewById(R.id.searchFinish)
        searchInput = findViewById(R.id.searchInput)

        searchClear.visibility = View.INVISIBLE

        searchFinish.clickView {
            if (context is SupportActivity)
                context.onBackPressed()
        }

        searchClear.setOnClickListener {
            searchInput.setText("")
            showKeyboard()
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                textWatcher?.afterTextChanged(text)
            }

            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                textWatcher?.beforeTextChanged(text, start, count, after)
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                searchClear.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
                textWatcher?.onTextChanged(text, start, before, count)
            }
        })
    }

    fun hideFinishButtom() {
        searchFinish.visibility = View.GONE
    }

    fun looseInputFocus() {
        searchInput.clearFocus()
    }

    /**
     *
     */
    fun setInputHintText(hint: String?) {
        hint?.apply {
            searchInput.hint = this
        }
    }

    fun setInputText(text: String) {
        isFromManuSet = true
        searchInput.setText(text)
        searchInput.setSelection(text.length)
    }

    /**
     *
     */
    fun clearInput() {
        searchInput.setText("")
    }

    fun showKeyboard() {
        searchInput.requestFocus()
        KeyBoardUtils.showKeyboard(ctx as AppCompatActivity, searchInput)
    }

    /**
     *
     */
    fun closeKeybord() {
        searchInput.clearFocus()
        KeyBoardUtils.closeKeybord(searchInput, ctx)
    }

    /**
     *
     */
    fun addTextChangedListener(textWatcher: TextWatcher) {
        this.textWatcher = textWatcher
    }

    /**
     *
     */
    fun onInputTextFocusChangeListener(onFocusListener: (String, Boolean) -> Unit) {
        searchInput.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                onFocusListener(
                    searchInput.text.toString(),
                    hasFocus
                )
            }
    }

    /**
     *用Rxjava 延时加载这个数据
     * @needClick 是否需要点击才触发搜索
     */
    fun setOnSearchListener(
        needClick: Boolean = false,
        listener: ((text: String, clickSearch: Boolean) -> Unit)?
    ): Disposable {

        if (needClick) {
            searchInput.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchInput.setOnEditorActionListener { _, _, _ ->
                listener?.invoke(searchInput.text.toString(), true)
                closeKeybord()
                true
            }
        } else {
            searchInput.imeOptions = EditorInfo.IME_ACTION_NONE
        }

        return RxTextView.textChanges(searchInput)
            .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe {
                if (isFromManuSet) {
                    isFromManuSet = false
                    if (!needNotifyWhenAutoAtManuSet)
                        return@subscribe
                }
                listener?.invoke(it.toString(), false)
            }
    }
}
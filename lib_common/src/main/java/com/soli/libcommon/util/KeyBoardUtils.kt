package com.soli.libcommon.util

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

/**
 *打开或关闭软键盘
 * @author Soli
 * @Time 2020/3/20 14:14
 */
object KeyBoardUtils {


    /**
     * 本段代码用来处理如果输入法还显示的话就消失掉输入键盘
     */
    fun dismissSoftKeyboard(activity: AppCompatActivity) {
        try {
            val inputMethodManage =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManage.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
        }

    }

    /**
     * 显示键盘
     *
     * @param view
     */
    fun showKeyboard(activity: AppCompatActivity, view: View) {
        try {
            val inputMethodManage =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManage.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    fun openKeybord(mEditText: EditText, mContext: Context) {
        try {
            val imm = mContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) {
                mEditText.requestFocus()
                imm.showSoftInput(mEditText, 0)
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    fun closeKeybord(mEditText: EditText, mContext: Context) {
        try {
            val imm = mContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * 判断点击位置是不是在输入区域
     */
    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return when {
                event.x > left && event.x < right
                        && event.y > top && event.y < bottom -> // 点击的是输入框区域，保留点击EditText的事件
                    false
                else -> true
            }
        }
        return false
    }
}
package com.soli.libcommon.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View

/**
 *
 * @author Soli
 * @Time 2019-08-09 11:22
 */

/**
 *
 */
fun Context.dip2px(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}

fun View.clickView(listener: ((View) -> Unit)?) {
    RxJavaUtil.click(this, listener)
}

/**
 * 屏幕宽度
 */
inline val Context.ScreenWidth: Int
    get() = this.applicationContext.resources.displayMetrics.widthPixels

/**
 * 屏幕高度
 */
inline val Context.ScreenHeight: Int
    get() = this.applicationContext.resources.displayMetrics.heightPixels

/**
 *
 */
fun Context.start(intent: Intent, requestCode: Int) {
    if (requestCode == -1)
        startActivity(intent)
    else if (this is Activity)
        this.startActivityForResult(intent, requestCode)
}

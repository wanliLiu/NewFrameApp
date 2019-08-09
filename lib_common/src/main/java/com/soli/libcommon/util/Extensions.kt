package com.soli.libcommon.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue

/**
 *
 * @author Soli
 * @Time 2019-08-09 11:22
 */

/**
 *
 */
fun Context.dip2px(dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
}

/**
 *
 */
fun Context.start(intent: Intent, requestCode: Int) {
    if (requestCode == -1)
        startActivity(intent)
    else if (this is Activity)
        this.startActivityForResult(intent, requestCode)
}

/**
 *
 */
inline fun <reified T : Activity> Context.openActivity(requestCode: Int = -1) {
    start(Intent(this, T::class.java), requestCode)
}

/**
 *
 */
inline fun <reified T : Activity> Context.openActivity(vararg params: Pair<String, String>, requestCode: Int = -1) {
    start(Intent(this, T::class.java).also { intent ->
        params.forEach { intent.putExtra(it.first, it.second) }
    }, requestCode)
}

/**
 *
 */
inline fun <reified T : Activity> Context.openActivity(
    vararg params: Pair<String, String>,
    requestCode: Int = -1, extern: Intent.() -> Unit
) {
    start(Intent(this, T::class.java).also { intent ->
        extern(intent)
        params.forEach { intent.putExtra(it.first, it.second) }
    }, requestCode)
}

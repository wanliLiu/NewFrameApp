package com.soli.libcommon.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import java.lang.reflect.InvocationHandler
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author Soli
 * @Time 2019-08-09 11:22
 */

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
inline fun Context.dip2px(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}

/**
 *
 */
inline fun Context.dip2pxPlus(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    )
}

inline fun View.clickView(noinline listener: (View) -> Unit) {
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
inline fun Context.showToast(
    msg: Any,
    time: Int = Toast.LENGTH_SHORT,
    useSystemDefault: Boolean = false
) {
    val content =
        if (msg is Int) if (msg > 0) this.resources.getString(msg) else "" else msg.toString()
    if (TextUtils.isEmpty(content))
        return

    ToastUtils.showShortToast(content)

//    if (useSystemDefault) {
//        Toast.makeText(this, content, time).apply {
//            setGravity(Gravity.CENTER, 0, 0)
//        }.show()
//    } else {
//        val toast = Toast(this).apply {
//            duration = time
//            setGravity(Gravity.CENTER, 0, 0)
//        }
//        toast.view = View.inflate(this, R.layout.item_toast, null).apply {
//            findViewById<TextView>(R.id.tosatText).text = content
//        }
//        toast.show()
//    }
}

/**
 * 程序是否在前台运行
 */
inline val Context.isAppOnForeground: Boolean
    get() {
        val value = try {
            val manager =
                this.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val appProcess = manager?.runningAppProcesses
            if (appProcess == null)
                false
            else
                appProcess.find { it.processName == packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND } != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

        MLog.d("app运行在前台：$value")

        return value
    }

/**
 * 用来那些不需要全部继承方法的
 */
inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    val noOpHandler = InvocationHandler { _, _, _ ->
        // no op
    }
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), noOpHandler
    ) as T
}

/**
 * 获取版本号
 */
inline fun Context.appVersion(): String {
    return try {
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, 0)
        info.versionName
    } catch (e: Exception) {
        "0.0"
    }
}

inline fun String.md5String(): String = Utils.MD5(this)
inline fun String?.isEmpty() = this == null || this.length == 0
inline fun String?.timeFormat(format: String = "yyyy-MM-dd"): String {
    if (TextUtils.isEmpty(this)) return ""
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    val date = df.parse(this)
    val df1 = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK)
    val date1 = df1.parse(date.toString())
    return SimpleDateFormat(format, Locale.CHINA).format(date1)
}

/**
 *  GSON
 */
inline fun <reified T> Any.toKJsonString(): String = Gson().toJson(this, T::class.java)
inline fun <reified T> String.toKJSONObject(): T? = Gson().fromJson(this, T::class.java)
inline fun <reified T> String.toKJSONArray(): List<T>? =
    Gson().fromJson(this, ParameterizedTypeImpl(T::class.java))

inline fun <T> String.toKJSONArray(clz: Class<*>): List<T>? =
    Gson().fromJson(this, ParameterizedTypeImpl(clz))

class ParameterizedTypeImpl(private val clz: Class<*>) : ParameterizedType {
    override fun getRawType(): Type = List::class.java
    override fun getOwnerType(): Type? = null
    override fun getActualTypeArguments(): Array<Type> = arrayOf(clz)
}

/**
 * Parcel
 */
inline fun <reified T> Parcel.readMutableList(): MutableList<T> {
    @Suppress("UNCHECKED_CAST")
    return readArrayList(T::class.java.classLoader) as MutableList<T>
}

inline fun Parcel.readKString(): String {
    return readString() ?: ""
}

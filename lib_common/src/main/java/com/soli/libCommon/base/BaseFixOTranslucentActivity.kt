package com.soli.libCommon.base

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.soli.libCommon.util.MLog


/**
 *  Android O (8.0) 窗体设置透明 然后又固定方向的问题</p>
 *  Only fullscreen opaque activities can request orientation
 * @author Soli
 * @Time 2019/4/2 09:55
 */
open class BaseFixOTranslucentActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            MLog.e("fixOrientation", "onCreate fixOrientation when Oreo, result = ${fixOrientation()}")
        }

        super.onCreate(savedInstanceState)
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            MLog.e("fixOrientation", "avoid calling setRequestedOrientation when Oreo.")
            return
        }
        super.setRequestedOrientation(requestedOrientation)
    }

    /**
     * 如果透明，直接把方向改为SCREEN_ORIENTATION_UNSPECIFIED
     */
    private fun fixOrientation(): Boolean {
        try {
            val field = Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            val o = field.get(this) as ActivityInfo
            o.screenOrientation = -1
            field.isAccessible = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 利用反射获取窗体是否是透明
     */
    private fun isTranslucentOrFloating(): Boolean {
        var isTranslucentOrFloating = false
        try {
            val styleableRes =
                Class.forName("com.android.internal.R\$styleable").getField("Window").get(null) as IntArray
            val ta = obtainStyledAttributes(styleableRes)
            val m = ActivityInfo::class.java.getMethod("isTranslucentOrFloating", TypedArray::class.java)
            m.isAccessible = true
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isTranslucentOrFloating
    }
}
package com.soli.newframeapp.access

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import com.facebook.stetho.common.LogUtil
import com.soli.libcommon.util.exec

object AccessibilityUtil {
    /**
     * 自动开启，前提是通过adb开启权限后
     * adb shell pm grant package android.permission.WRITE_SECURE_SETTINGS
     */
    fun autoOpen(
        context: Context,
        packageName: String,
        serviceClass: Class<*>
    ): Boolean {
        return try {
            "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS".exec {  }
            //自动授权无障碍
            Settings.Secure.putString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                "$packageName/${serviceClass.name}"
            )
            Settings.Secure.putInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED, 1
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 是否开启
     */
    fun isOpen(
        mContext: Context,
        packageName: String,
        serviceClass: Class<*>
    ): Boolean {
        val service = "$packageName/${serviceClass.name}"
        var accessibilityEnabled = 0

        val accessibilityFound = false
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            LogUtil.e(e, "")
        }

        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return accessibilityFound
    }
}
package com.soli.newframeapp.access

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import com.soli.libcommon.util.MLog

class KiwiAccessibilityService : AccessibilityService() {

    companion object {
        var instance: KiwiAccessibilityService? = null

        val TAG = KiwiAccessibilityService.javaClass.simpleName

        @JvmStatic
        fun startService(context: Context) {
            AccessibilityUtil.autoOpen(
                context,
                context.packageName,
                KiwiAccessibilityService::class.java
            ).apply {
                MLog.d(TAG,"开启无障碍：$this")
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        MLog.d(TAG,"onServiceConnected：${javaClass.simpleName}")
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        eventCallBack.forEach { it.invoke(event) }
        if (event.packageName == "android") {
            instance?.findNode("android:id/aerr_close")?.apply {
                instance?.performClick(this)
            }
        }
    }

    override fun onInterrupt() {
        instance = null
        MLog.d(TAG,"onInterrupt：${javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
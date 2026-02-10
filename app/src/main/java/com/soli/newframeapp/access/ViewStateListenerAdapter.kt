package com.soli.newframeapp.access

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.kiwisec.floatwindow.FloatWindow
import com.kiwisec.floatwindow.ViewStateListener
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.StatusBarUtil
import com.soli.libcommon.util.dip2px


/**
 *
 * @Author:        zhazha
 * @CreateDate:    2021/4/9 4:55 下午
 * @Description:   FloatWindow监听
 */
class ViewStateListenerAdapter(private val tag: String) : ViewStateListener {

    companion object {
        val viewSize by lazy {
            Constant.context.dip2px(40)
        }
    }

    private var lastScreenWidth: Int = 0
    private var lastScreenHeight: Int = 0
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    private val statusBarHeight = StatusBarUtil.getStatusBarHeight(Constant.context)

    private val configChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_CONFIGURATION_CHANGED) return
            updateFloatWindowPositionsIfNeeded()
        }
    }

    init {
        initDefaultSize()
        ContextCompat.registerReceiver(
            Constant.context,
            configChangeReceiver,
            IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED),
            ContextCompat.RECEIVER_EXPORTED
        )
    }


    private fun updateFloatWindowPositionsIfNeeded() {
        val point = getScreenSize() ?: return
        val width = point.x
        val height = point.y
        if (width <= 0 || height <= 0) return

        if (width != lastScreenWidth || height != lastScreenHeight) {
            initDefaultSize()
            FloatWindow.get("control")?.apply {
                updateX(0)
                updateY((height * 0.4f).toInt().coerceAtLeast(0))
            }
            FloatWindow.get("click")?.apply {
                val x =
                    if (width > height) width - viewSize - statusBarHeight else width - viewSize
                updateX(x.coerceAtLeast(0))
                updateY((height * 0.3f).toInt().coerceAtLeast(0))
            }
        }
        lastScreenWidth = width
        lastScreenHeight = height
    }


    private fun initDefaultSize() {
        getScreenSize()?.apply {
            val width = this.x
            val height = this.y
            if (width <= 0 || height <= 0) return
            if (width > height) {
                // 横屏
                xMin = 0
                yMin = 0
                xMax = width - viewSize  - statusBarHeight
                yMax = height - viewSize
            } else {
                //竖屏
                xMin = 0
                yMin = 0
                xMax = width - viewSize
                yMax = height - viewSize - statusBarHeight
            }
        }
    }

    private fun getScreenSize(): Point? {
        val wm = Constant.context.getSystemService(WindowManager::class.java) ?: return null
        val point = Point()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getSize(point)
        return point
    }


    override fun onPositionUpdate(x: Int, y: Int) {
        if (x < xMin) {
            FloatWindow.get(tag)?.updateX(xMin)
        }
        if (x > xMax) {
            FloatWindow.get(tag)?.updateX(xMax)
        }
        if (y < yMin) {
            FloatWindow.get(tag)?.updateY(yMin)
        }
        if (y > yMax) {
            FloatWindow.get(tag)?.updateY(yMax)
        }
    }

    override fun onShow() {
    }

    override fun onHide() {
    }

    override fun onDismiss() {
    }

    override fun onMoveAnimStart() {
    }

    override fun onMoveAnimEnd() {
    }

    override fun onBackToDesktop() {
    }
}

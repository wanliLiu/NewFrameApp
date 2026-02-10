package com.soli.newframeapp.access

import com.kiwisec.floatwindow.FloatWindow
import com.kiwisec.floatwindow.ViewStateListener
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.ScreenHeight
import com.soli.libcommon.util.ScreenWidth
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

    private val xMin = 0
    private val xMax = Constant.context.ScreenWidth - viewSize
    private val yMin = 0
    private val yMax = Constant.context.ScreenHeight - viewSize
    override fun onPositionUpdate(x: Int, y: Int) {
//        Logger.d("onPositionUpdate x = $x y = $y xMin = $xMin xMax = $xMax yMin = $yMin yMax = $yMax")
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
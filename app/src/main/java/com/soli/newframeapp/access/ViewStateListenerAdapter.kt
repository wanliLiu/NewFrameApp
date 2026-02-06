package com.soli.newframeapp.access

import com.kiwisec.floatwindow.FloatWindow
import com.kiwisec.floatwindow.ViewStateListener


/**
 *
 * @Author:        zhazha
 * @CreateDate:    2021/4/9 4:55 下午
 * @Description:   FloatWindow监听
 */
class ViewStateListenerAdapter : ViewStateListener {
    override fun onPositionUpdate(x: Int, y: Int) {
        if (x < -30) {
            FloatWindow.get().updateX(-30)
        }
        if (x > 1000) {
            FloatWindow.get().updateX(1000)
        }
        if (y < -80) {
            FloatWindow.get().updateY(-80)
        }
        if (y > 1860) {
            FloatWindow.get().updateY(1860)
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
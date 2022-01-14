package com.soli.newframeapp.access

import com.facebook.stetho.common.LogUtil

fun sleep(time: Long): Boolean {
    return try {
        if (!Thread.currentThread().isAlive) return false
        if (Thread.currentThread().isInterrupted) return false
        Thread.sleep(time)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 暂停回复控制
 */
class PauseControl {

    private var paused = false

    fun reset() {
        paused = false
    }

    fun change(onPaused: Boolean) {
        paused = onPaused
        LogUtil.d("onPaused： $onPaused")
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    /**
     * 检测等待
     * @param interval 间隔时间ms
     * @return 返回等待时间
     */
    fun checkWait(interval: Long = 500): Long {
        val startTime = System.currentTimeMillis()
        while (true) {
            if (paused) {
                try {
                    if (!sleep(interval))
                        break
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            } else {
                break
            }
        }
        return System.currentTimeMillis() - startTime
    }

    fun isPause(): Boolean {
        return paused
    }
}
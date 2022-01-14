package com.soli.newframeapp.access

import android.accessibilityservice.AccessibilityService
import com.soli.libcommon.util.MLog
import io.reactivex.rxjava3.core.Observable
import kotlin.concurrent.thread

/**
 * 自动化遍历点击
 */
object AutoClickObservable {

     val TAG = AutoClickObservable.javaClass.simpleName

    /**
     *  @param service 无障碍服务
     *  @param duration
     *  @param packageName 包名
     *  @param isEnd 是否结束
     */
    fun newInstance(
        service: AccessibilityService,
        packageName: String,
        duration: Long,
        isEnd: () -> Boolean,
        pauseControl: PauseControl,
        permissionCLick: Boolean = false
    ): Observable<Boolean> = Observable.create { observer ->

        /**
         * 可点击packageName白名单
         */
        val whitelistPackageName = arrayOf(
            packageName
        )

        if (permissionCLick) {
            thread {
                while (!observer.isDisposed && !isEnd() && sleep(1000)) {
                    pauseControl.checkWait()
                    service.findNode("com.android.packageinstaller:id/permission_allow_button")
                        ?.apply {
                            pauseControl.checkWait()
                            service.performClick(this)
                        }
                }
            }
        }



        try {
            MLog.d(TAG, "开始自动点击")
            var startTime = System.currentTimeMillis()
            while ((System.currentTimeMillis() - startTime) < duration
                && !observer.isDisposed
                && Thread.currentThread().isAlive
                && !Thread.currentThread().isInterrupted
                && !isEnd()
            ) {
                startTime += pauseControl.checkWait()
                if (!sleep(1000)) break

                startTime += pauseControl.checkWait()


                val clickNodes = service.findNodes(
                    clickable = true,
//                    root = service.rootInActiveWindow,
                    visible = true
                )
                    .filter {
                        when (it.viewIdResourceName) {
                            "com.android.systemui:id/home",
                            "com.android.systemui:id/back",
                            "com.android.systemui:id/recent_apps"
                            -> false
                            else -> true
                        }
                    }
                    .filter { !pauseControl.isPause() }
                    .filter { !isEnd() }
                    .filter { whitelistPackageName.contains(it.packageName) }
                startTime += pauseControl.checkWait()
                when {
                    clickNodes.size == 1 -> {
                        service.performClick(clickNodes.first())
                    }
                    clickNodes.isEmpty() -> {
                        MLog.d(TAG, "可点击node为空")
                        service.findNodes(scrollable = true, visible = true)
                            .randomOrNull()
                            .apply {
                                if (this == null) {
                                    MLog.d(TAG, "performBack")
                                    service.performBack()
                                } else {
                                    MLog.d(TAG, "performScrollForward")
                                    service.performScrollForward(this)
                                }
                            }
                    }
                    else -> {
                        clickNodes.randomOrNull()
                            ?.apply {
                                service.performClick(this)
                            }
                    }
                }
                startTime += pauseControl.checkWait()
                if (!sleep(200)) break
            }

            observer.onNext(true)
        } catch (e: Exception) {
            observer.onError(e)
        } finally {
            observer.onComplete()
            MLog.d(TAG, "AutoCLick onComplete")
        }
    }
}
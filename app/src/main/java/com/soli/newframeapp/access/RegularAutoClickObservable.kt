package com.soli.newframeapp.access

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.MLog
import com.soli.libcommon.util.md5String
import com.soli.newframeapp.util.AppUtils
import io.reactivex.rxjava3.core.Observable
import kotlin.concurrent.thread

/**
 * 有规律的自动点击实现方式
 */
class RegularAutoClickObservable(
    private val service: AccessibilityService,
    private val packageName: String,
    private val isEnd: () -> Boolean,
    private val pauseControl: PauseControl,
    private val permissionCLick: Boolean = false,
    private val duration: Long = 15 * 60 * 1000L, //还是要限制最大的时间，不然有些情况下点击死了
) {

    companion object {
        val TAG = RegularAutoClickObservable::class.java.simpleName
    }

    private var startTime = System.currentTimeMillis()

    //每次机测自动点击的最多页面数量 最终执行的次数是 maxDetectWindowsCount * maxClickCount
    private val maxDetectWindowsCount = 50
    private val maxClickCount = 30
    private var windowIndex = 0

    private var preNode: ActivityModel? = null

    @Keep
    private data class ActivityModel(
        val mId: Int,
        val viewMd5: String,
        var clickIndex: Int,
        var canClickList: MutableList<AccessibilityNodeInfo>,
        val window: AccessibilityWindowInfo
    ) {
        override fun toString(): String {
            return "mId: $mId viewMd5: $viewMd5 clickIndex: $clickIndex canClickList: ${canClickList.size} windows: ${window.hashCode()}"
        }
    }

    /**
     *
     */
    private fun dumpWindowsListStr(): String {
        val sp = StringBuilder("\n")
        var index = 0
        windowsList.forEach {
            sp.appendLine("[${index}]:($it)")
            index++
        }
        sp.appendLine()
        return sp.toString()
    }

    //每个window root node
    private val windowsList = mutableListOf<ActivityModel>()

    /**
     * 回到正在检测的app界面
     */
    private fun backToTargetApp() {
        if (AppUtils.getTopApp(service) != packageName) {
            AppUtils.backToApp(Constant.context, packageName)
        }
    }

    /**
     *
     */
    private fun performBack() {
        service.findNode(text = Regex("返回"))?.apply {
            pauseControl.checkWait()
            service.performClick(this)
        }
        service.performBack()
        backToTargetApp()
        sleep(1300)
    }

    /**
     *实际的点击操作
     */
    private fun doActualClick() {
        val index = windowIndex - 1
        val data = if (index >= 0) windowsList[index] else return
        MLog.d(TAG, "doActualClick--->($data)")
        pauseControl.checkWait()
        if (data.clickIndex < data.canClickList.size && data.clickIndex < maxClickCount) {
            backToTargetApp()
//            val model = data.canClickList[data.canClickList.size - data.clickIndex - 1]
            val model = data.canClickList[data.clickIndex]
            when (model.viewIdResourceName) {
                "com.android.systemui:id/home",
                "com.android.systemui:id/back",
                "com.android.systemui:id/recent_apps"
                -> MLog.d(TAG, "规律点击过程中屏蔽点击home back recent_apps")
                else -> {
                    service.performClick(model)
                    data.clickIndex += 1
                    MLog.d(TAG, "当前页面的点击次数：${data.clickIndex} 点击的节点信息：$model")
                    pauseControl.checkWait()
                }
            }
        } else {
            MLog.d(
                TAG,
                "当前页面达到最大限制点击次数：$maxClickCount 当前页面可点击的数量：${data.canClickList.size} 执行返回界面操作"
            )
            performBack()
            windowIndex--
        }
    }

    /**
     *  当前组件使能，并且可见，只要可点击、可聚集、可选择都算能点击的组件
     */
    private fun decideCanClick(
        node: AccessibilityNodeInfo,
        list: MutableList<AccessibilityNodeInfo>
    ) {
        if (node.isEnabled && node.isVisibleToUser) {
            if (node.isClickable) {//|| node.isFocusable || node.isCheckable
                list.add(node)
            }
        }
    }


    /**
     *
     */
    private fun addNode(node: AccessibilityNodeInfo, json: JSONObject) {
        json["packageName"] = node.packageName ?: ""
        json["className"] = node.className ?: ""
//        json["text"] = node.text ?: ""
//        json["contentDescription"] = node.contentDescription ?: ""
//        json["childCount"] = node.childCount
//        json["checkable"] = node.isCheckable
//        json["checked"] = node.isChecked
//        json["focusable"] = node.isFocusable
//        json["focused"] = node.isFocused
//        json["selected"] = node.isSelected
//        json["clickable"] = node.isClickable
//        json["enabled"] = node.isEnabled
//        json["scrollable"] = node.isScrollable
//        json["visible"] = node.isVisibleToUser
        json["viewIdResName"] = node.viewIdResourceName ?: ""
//        var zoom = Rect()
//        node.getBoundsInScreen(zoom)
//        json["boundsInParent"] = zoom.toString()
//        node.getBoundsInParent(zoom)
//        json["boundsInScreen"] = zoom.toString()
    }

    /**
     *
     */
    private fun dumpHierachry(
        node: AccessibilityNodeInfo,
        canClickNode: MutableList<AccessibilityNodeInfo>
    ): JSONObject {
        val hierarchy = JSONObject()
        if (node.childCount > 0) {
            addNode(node, hierarchy)
            decideCanClick(node, canClickNode)
            val array = JSONArray()
            for (index in 0 until node.childCount) {
                node.getChild(index)?.apply { array.add(dumpHierachry(this, canClickNode)) }
            }
            hierarchy["childs"] = array
        } else {
            addNode(node, hierarchy)
            decideCanClick(node, canClickNode)
        }

        return hierarchy
    }

    /**
     *  @param service 无障碍服务
     *  @param duration
     *  @param packageName 包名
     *  @param isEnd 是否结束
     */
    fun newInstance(): Observable<Boolean> = Observable.create { observer ->
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
            startTime = System.currentTimeMillis()
            MLog.d(TAG, "开始有规则的自动点击，能允许最多点击时间：${duration / (60 * 1000L)}分钟")
            while ((System.currentTimeMillis() - startTime) < duration &&
                !observer.isDisposed
                && Thread.currentThread().isAlive
                && !Thread.currentThread().isInterrupted
                && !isEnd()
            ) {
                pauseControl.checkWait()
                backToTargetApp()
                if (!sleep(2000)) break
                pauseControl.checkWait()

                val tmpWindows = service.windows.firstOrNull { it.isActive } ?: continue
                val canClicklist = mutableListOf<AccessibilityNodeInfo>()
                tmpWindows.root ?: continue
                val hierachery = dumpHierachry(tmpWindows.root, canClicklist).toJSONString()
                val currentHierachery = hierachery.md5String()
                if (canClicklist.size == 0) {
                    MLog.d(TAG, "当前窗口视图可点击数量为0，有可能当前正在请求数据")
                    continue
                }

                val haveSameWindow = windowsList.indexOfLast { tmpWindows.hashCode() == it.mId }
                if (haveSameWindow != -1) {
                    sleep(2000)
                    MLog.d(TAG, "单Activity多framgent情况，这种延时后在进行获取一下")
                    continue
                }

                val haveExist =
                    windowsList.indexOfLast { it.mId == tmpWindows.id && it.viewMd5 == currentHierachery }

                MLog.d(
                    TAG,
                    "haveExist: $haveExist current hash = ${tmpWindows.hashCode()} currentHierachery=$currentHierachery  windowIndex = $windowIndex windowsList.size = ${windowsList.size} canClicklist = ${canClicklist.size} \n cacheWindowList: ${dumpWindowsListStr()} \n current Activity window: $this"
                )
                if (windowsList.size < maxDetectWindowsCount) {
                    pauseControl.checkWait()

                    if (haveExist == -1) {
                        windowsList.add(
                            ActivityModel(
                                tmpWindows.hashCode(),
                                currentHierachery,
                                0,
                                canClicklist,
                                tmpWindows
                            )
                        )
                        windowIndex++
                        MLog.d(
                            TAG,
                            "新页面添加,当前待点击的页面数量：$windowIndex 可点击的视图数量：${canClicklist.size} windowIndex=$windowIndex \n cacheWindowList: ${dumpWindowsListStr()}"
                        )
                        doActualClick()
                        pauseControl.checkWait()
                    } else {
                        MLog.d(TAG, "当前页面已存在，更新数据")
                        windowsList[haveExist].canClickList = canClicklist
                        windowIndex = haveExist + 1
                        doActualClick()
                        pauseControl.checkWait()
                    }
                } else if (windowsList.size == maxDetectWindowsCount) {
                    if (haveExist == -1) {
                        //达到最大点击页面说，然后又有新的页面点击，这个时候先退回，然后再点击
                        MLog.d(TAG, "达到最多点击的页面数：$maxDetectWindowsCount 又有新的页面产生，先回退再点击")
                        if (windowIndex == 1) {
                            performBack()
                        }
                    } else {
                        windowIndex = haveExist + 1
                        doActualClick()
                    }
                }

                if (windowIndex <= 0) {
                    break
                }

                pauseControl.checkWait()
                if (!sleep(500)) break

                MLog.d(TAG, "自动点击进行了：${(System.currentTimeMillis() - startTime) / 1000}s ")
            }

            observer.onNext(true)
        } catch (e: Exception) {
            e.printStackTrace()
            MLog.e(TAG, e.message)
            observer.onError(e)
        } finally {
            MLog.d(TAG, "自动点击结束")
            observer.onComplete()
        }
    }
}
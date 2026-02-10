package com.soli.newframeapp.access

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.logger.Logger
import com.soli.libcommon.base.Constant
import com.soli.libcommon.util.md5String
import com.soli.newframeapp.util.AppUtils
import io.reactivex.rxjava3.core.Observable
import kotlin.concurrent.thread

/**
 * 以页面为维度，实现点击
 */
class AutoClickByHierachryObservable(
    private val service: AccessibilityService,
    private val packageName: String,
    private val isEnd: () -> Boolean,
    private val pauseControl: PauseControl,
    private val permissionCLick: Boolean = false,
    private val duration: Long = 10 * 60 * 1000L, //还是要限制最大的时间，不然有些情况下点击死了
) {

    companion object {
        val TAG = AutoClickByHierachryObservable::class.java.simpleName
    }

    private var startTime = System.currentTimeMillis()

    private val NAF_EXCLUDED_CLASSES = arrayListOf(
        "ScrollView",
        "GridView",
        "GridLayout",
        "RecyclerView",
        "ListView",
        "TableLayout",
        "NestedScrollView"
    )

    private val listLayoutExclude: Regex by lazy {
        Regex(NAF_EXCLUDED_CLASSES.joinToString(separator = "|"))
    }

    //每次机测自动点击的最多页面数量 最终执行的次数是 maxDetectWindowsCount * maxClickCount
    private val maxDetectWindowsCount = 100
    private val maxClickCount = 30
    private var windowIndex = 0

    //没有可以点击的试图连续多少次退出
    private val noneClickMax = 2
    private var noneIndex = 0

    //每个window root node
    private val windowsList = mutableListOf<ActivityModel>()

    @Keep
    private data class ClickModel(
        var markClick: Boolean,
        val node: AccessibilityNodeInfo,
    ) {
        constructor(node: AccessibilityNodeInfo) : this(false, node)
    }

    @Keep
    private data class ActivityModel(
        val mId: Int,
        val viewMd5: String,
        var clickIndex: Int,
        var nextClickIndex: Int,
        var canClickList: MutableList<ClickModel>,
        val packageName: String,
    ) {
        override fun toString(): String {
            return "mId: $mId viewMd5: $viewMd5 clickIndex: $clickIndex canClickList: ${canClickList.size} packageName: $packageName"
        }

        /**
         * 查找没有点击的
         */
        fun forClickNode(): AccessibilityNodeInfo {
            //过滤没有点击的
            val notClickList = canClickList.filter { !it.markClick }
            if (notClickList.size == 1) {
                return notClickList.first().let {
                    it.markClick = true
                    it.node
                }
            }

            return notClickList.random().let {
                it.markClick = true
                it.node
            }
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
    private fun doActualClick(model: ActivityModel? = null) {
        val index = windowIndex - 1
        val data = when (model) {
            null -> if (index >= 0) windowsList[index] else return
            else -> model
        }
        Logger.d("doActualClick--->($data)")
        pauseControl.checkWait()
        if (data.packageName == packageName && data.clickIndex < data.canClickList.size && data.clickIndex < maxClickCount) {
            backToTargetApp()

            //当前可点击节点倒序点击
//            val model = data.canClickList[data.canClickList.size - data.clickIndex - 1].let {
//                it.markClick = true
//                it.node
//            }

            //当前可点击节点正序点击
            val model = data.canClickList[data.clickIndex].let {
                it.markClick = true
                it.node
            }

            //当前可点击节点随机点击
//            val scrollAbleIndex = data.canClickList.indexOfFirst { it.node.isScrollable }
//            val model = if (scrollAbleIndex != -1 && !data.canClickList[scrollAbleIndex].markClick)
//                data.canClickList[scrollAbleIndex].node else data.forClickNode()

            //默认的方式
//            val model = data.forClickNode()

            data.clickIndex += 1
            when (model.viewIdResourceName) {
                "com.android.systemui:id/home",
                "com.android.systemui:id/back",
                "com.android.systemui:id/recent_apps",
//                "com.soli.newframeapp:id/barBackIcon"
                -> Logger.d("规律点击过程中屏蔽点击home back recent_apps")
                else -> {
                    if (model.isScrollable)
                        service.performScrollForward(model)
                    else
                        service.performClick(model)
                    Logger.d("当前页面的点击次数：${data.clickIndex} 当前事件类型：isClickable = ${model.isClickable} isScrollable = ${model.isScrollable} \n点击的节点信息：$model")
                    pauseControl.checkWait()
                }
            }
        } else {
            if (data.packageName == packageName) {
                Logger.d(
                    "当前页面达到最大限制点击次数：$maxClickCount 当前页面可点击的数量：${data.canClickList.size} 执行返回界面操作"
                )
            } else {
                Logger.d("只点击被测app $packageName 的界面，其他界面直接返回,并排除去")
                windowsList.remove(data)
            }

            performBack()
            windowIndex--
        }
    }

    /**
     * This should be used when it's already determined that the node is NAF and
     * a further check of its children is in order. A node maybe a container
     * such as LinerLayout and may be set to be clickable but have no text or
     * content description but it is counting on one of its children to fulfill
     * the requirement for being accessibility friendly by having one or more of
     * its children fill the text or content-description. Such a combination is
     * considered by this dumper as acceptable for accessibility.
     *
     * @param node
     * @return false if node fails the check.
     */
    private fun childNafCheck(node: AccessibilityNodeInfo): Boolean {
        val childCount = node.childCount
        for (x in 0 until childCount) {
            val childNode = node.getChild(x)
            if (childNode == null) {
                Logger.d(
                    String.format(
                        "Null child %d/%d, parent: %s",
                        x,
                        childCount,
                        node.toString()
                    )
                )
                continue
            }
            if (safeCharSeqToString(childNode.contentDescription).isNotEmpty()
                || safeCharSeqToString(childNode.text).isNotEmpty()
            )
                return true
            if (childNafCheck(childNode)) return true
        }
        return false
    }


    /**
     *  当前组件使能，并且可见，只要可点击、可聚集、可选择都算能点击的组件
     */
    private fun decideCanClick(
        node: AccessibilityNodeInfo,
        list: MutableList<ClickModel>
    ) {
        if (node.isEnabled && node.isVisibleToUser) {
            if ((node.isClickable || node.isFocused) && !(safeCharSeqToString(node.className)).matches(listLayoutExclude)) {
                if (node.childCount > 0) {
                    if (safeCharSeqToString(node.viewIdResourceName).isNotEmpty() &&
                        childNafCheck(node)
                    ) {
                        list.add(ClickModel(node))
                    }
                } else if ((safeCharSeqToString(node.contentDescription).isNotEmpty()
                            || safeCharSeqToString(node.text).isNotEmpty()) &&
                    safeCharSeqToString(node.viewIdResourceName).isNotEmpty()
                ) {
                    list.add(ClickModel(node))
                }
            }
//            else if (node.isScrollable) {
//                list.add(ClickModel(node))
//            }
        }
    }

    /**
     *
     */
    private fun safeCharSeqToString(cs: CharSequence?) =
        if (cs == null) "" else stripInvalidXMLChars(cs)

    /**
     *
     */
    private fun stripInvalidXMLChars(cs: CharSequence): String {
        val xml10pattern =
            "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]"

        return cs.toString().replace(Regex(xml10pattern), "?")
    }

    /**
     *
     */
    private fun addNode(node: AccessibilityNodeInfo, json: JSONObject) {
        json["packageName"] = safeCharSeqToString(node.packageName)
        json["className"] = safeCharSeqToString(node.className)
        json["text"] = safeCharSeqToString(node.text)
        json["contentDescription"] = safeCharSeqToString(node.contentDescription)
        json["childCount"] = node.childCount
        json["checkable"] = node.isCheckable
        json["checked"] = node.isChecked
        json["focusable"] = node.isFocusable
        json["focused"] = node.isFocused
        json["selected"] = node.isSelected
        json["clickable"] = node.isClickable
        json["enabled"] = node.isEnabled
        json["scrollable"] = node.isScrollable
        json["visible"] = node.isVisibleToUser
        json["viewIdResName"] = safeCharSeqToString(node.viewIdResourceName)
        var zoom = Rect()
        node.getBoundsInScreen(zoom)
        json["boundsInParent"] = zoom.toString()
        node.getBoundsInParent(zoom)
        json["boundsInScreen"] = zoom.toString()
    }

    /**
     *
     */
    private fun dumpHierachry(
        node: AccessibilityNodeInfo,
        canClickNode: MutableList<ClickModel>
    ): JSONObject {
        val hierarchy = JSONObject()
        if (node.childCount > 0) {
            addNode(node, hierarchy)
            decideCanClick(node, canClickNode)
            val array = JSONArray()
            for (index in 0 until node.childCount) {
                node.getChild(index)?.apply {
                    if (this.isVisibleToUser) {
                        array.add(dumpHierachry(this, canClickNode))
                    }
                }
            }
            if (array.size > 0)
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
    fun observable(): Observable<Boolean> = Observable.create { observer ->
        if (permissionCLick) {
            thread {
                while (!observer.isDisposed && !isEnd() && sleep(1000)) {
                    pauseControl.checkWait()
                    service.clickPermission(pauseControl)
                }
            }
        }

        try {
            startTime = System.currentTimeMillis()
            Logger.d("开始有规则的自动点击，能允许最多点击时间：${duration / (60 * 1000L)}分钟")
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
                val rootNode = service.rootInActiveWindow
                val canClicklist = mutableListOf<ClickModel>()
                rootNode ?: continue
                val hierachery = dumpHierachry(rootNode, canClicklist).toJSONString()
                val currentHierachery = hierachery.md5String()
                if (canClicklist.size == 0) {
                    noneIndex++
                    Logger.d(
                        "当前窗口视图可点击数量为0，有可能当前正在请求数据->noneIndex=$noneIndex  noneClickMax = $noneClickMax"
                    )
                    if (noneIndex > noneClickMax) {
                        performBack()
                        noneIndex = 0
                    }
                    service.findNodes(scrollable = true, visible = true)
                        .randomOrNull()
                        .apply {
                            if (this == null) {
                                Logger.d("performBack")
                                performBack()
                            } else {
                                Logger.d("performScrollForward")
                                service.performScrollForward(this)
                            }
                        }
                    continue
                }

                val haveExist =
                    windowsList.indexOfLast { it.viewMd5 == currentHierachery }//it.mId == tmpWindows.id &&

                if (windowsList.size < maxDetectWindowsCount) {
                    pauseControl.checkWait()

                    if (haveExist == -1) {
                        val model = ActivityModel(
                            tmpWindows.hashCode(),
                            currentHierachery,
                            0, -1,
                            canClicklist,
                            rootNode.packageName?.toString() ?: ""
                        )
                        windowsList.add(model)
                        windowIndex++
                        Logger.d(
                            "新页面添加,当前待点击的页面数量：$windowIndex 可点击的视图数量：${canClicklist.size} windowIndex=$windowIndex}"
                        )
                        doActualClick(model)
                        pauseControl.checkWait()
                    } else {
                        Logger.d("当前页面已存在，更新数据")
                        windowsList[haveExist].canClickList = canClicklist
                        windowIndex = haveExist + 1
                        doActualClick()
                        pauseControl.checkWait()
                    }
                } else if (windowsList.size == maxDetectWindowsCount) {
                    if (haveExist == -1) {
                        //达到最大点击页面说，然后又有新的页面点击，这个时候先退回，然后再点击
//                        noneIndex++
//                        if (noneIndex > noneClickMax) {
                        doActualClick()
//                            noneIndex = 0
//                        }
                        Logger.d(" windowIndex=$windowIndex 达到最多点击的页面数：$maxDetectWindowsCount 又有新的页面产生，先回退再点击 ")// noneIndex=$noneIndex noneClickMax = $noneClickMax
                        performBack()
                    } else {
                        windowIndex = haveExist + 1
                        doActualClick()
                    }
                }

                Logger.d(
                    "haveExist: $haveExist current hash = ${tmpWindows.hashCode()} currentHierachery=$currentHierachery  windowIndex = $windowIndex windowsList.size = ${windowsList.size} canClicklist = ${canClicklist.size} \n\ncurrent Activity window: $this \n\ncacheWindowList: ${dumpWindowsListStr()}\n"
                )

                if (windowIndex <= 0) {
                    Logger.d("所有的页面点击完了，结束自动点击---->真实情况这个比较少")
                    break
                }

                pauseControl.checkWait()
                if (!sleep(500)) break

                Logger.d("点击的最多页数：$maxDetectWindowsCount 每个页面最多点击的次数：$maxClickCount 最多能点击的时间：${duration / (60 * 1000L)}分钟 目前自动点击进行了：${(System.currentTimeMillis() - startTime) / 1000}s 或 ${(System.currentTimeMillis() - startTime) / (1000 * 60)}分钟")
            }

            observer.onNext(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.e(e.message ?: "")
            observer.onError(e)
        } finally {
            Logger.d("点击结束----》 点击的最多页数：$maxDetectWindowsCount 每个页面最多点击的次数：$maxClickCount 最多能点击的时间：${duration / (60 * 1000L)}分钟 自动点击结束,总耗时：${(System.currentTimeMillis() - startTime) / 1000}s 或 ${(System.currentTimeMillis() - startTime) / (1000 * 60)}分钟")
            observer.onComplete()
        }
    }
}

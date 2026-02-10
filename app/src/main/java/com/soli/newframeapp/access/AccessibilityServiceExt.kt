package com.soli.newframeapp.access

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

private val accEventCallBack = hashSetOf<(event: AccessibilityEvent) -> Unit>()

val AccessibilityService.eventCallBack: HashSet<(event: AccessibilityEvent) -> Unit>
    get() = accEventCallBack

/**
 * 注册event监听
 */
fun AccessibilityService.registerEvent(callBack: (event: AccessibilityEvent) -> Unit) {
    eventCallBack.add(callBack)
}

/**
 * 取消event监听
 */
fun AccessibilityService.unRegisterEvent(callBack: (event: AccessibilityEvent) -> Unit) {
    eventCallBack.remove(callBack)
}

/**
 * 遍历所有节点
 */
fun AccessibilityService.printAllNode(node: AccessibilityNodeInfo? = null) {
    if (node == null)
        foreachNodes { Log.d("AutoClickObservable", it.toString()) }
    else
        foreachNodes(node) { Log.d("AutoClickObservable", it.toString()) }
}

fun AccessibilityService.findNodesByText(
    root: AccessibilityNodeInfo = rootInActiveWindow,
    text: String,
    visible: Boolean? = null,
): List<AccessibilityNodeInfo> = findNodesByText(root, Regex(text), visible)

fun AccessibilityService.findNodesByText(
    root: AccessibilityNodeInfo = rootInActiveWindow,
    regex: Regex,
    visible: Boolean? = null,
): List<AccessibilityNodeInfo> {
    return findNodes(root = root, text = regex, visible = visible)
}

fun AccessibilityService.findNodeById(
    resId: String,
    visible: Boolean? = null,
    index: Int = 0,
): AccessibilityNodeInfo? {
    return findNode(resId, visible = visible, index = index)
}

fun AccessibilityService.findNodeById(
    resId: String,
    visible: Boolean? = null,
    index: Int = 0,
    root: AccessibilityNodeInfo?,
): AccessibilityNodeInfo? {
    return findNode(resId, visible = visible, index = index, root = root)
}

/**
 * 返回HOME界面
 */
fun AccessibilityService.performHome(): Boolean {
    return performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
}

/**
 * 返回
 */
fun AccessibilityService.performBack(): Boolean {
    return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
}

fun AccessibilityService.longClick(id: String): Boolean {
    return longClick(findNodeById(id))
}

/**
 * 长按
 */
fun AccessibilityService.longClick(
    nodeInfo: AccessibilityNodeInfo?,
    canClick: Boolean = true,
): Boolean {
    nodeInfo ?: return false

    return if (canClick) {
        if (nodeInfo.isClickable) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
        } else {
            longClick(nodeInfo.parent)
        }
    } else {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
    }
}

fun AccessibilityService.clickText(text: String): Boolean {
    return performClick(findNode(text = Regex(text)))
}

fun AccessibilityService.clickDesc(text: String, visible: Boolean? = null): Boolean {
    return performClick(findNode(description = Regex(text), visible = visible))
}

/**
 * 根据控件点击
 */
fun AccessibilityService.performClick(
    nodeInfo: AccessibilityNodeInfo?,
    canClick: Boolean = true,
): Boolean {
    nodeInfo ?: return false
    return if (canClick) {
        if (nodeInfo.isClickable) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            performClick(nodeInfo.parent)
        }
    } else {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }
}

/**
 * 根据id点击
 */
fun AccessibilityService.performClick(id: String): Boolean {
    return performClick(findNode(id))
}

fun AccessibilityService.performSelect(nodeInfo: AccessibilityNodeInfo?) {
    nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SELECT)
}

@RequiresApi(Build.VERSION_CODES.N)
fun AccessibilityService.gesture(path: Path, duration: Long): Boolean {
    val gestureDescription = GestureDescription.Builder()
        // 参数path：笔画路径
        // 参数startTime：时间 (以毫秒为单位)，从手势开始到开始笔划的时间，非负数
        // 参数duration：笔划经过路径的持续时间(以毫秒为单位)，非负数
        .addStroke(
            GestureDescription.StrokeDescription(
                path,
                100,
                duration
            )
        )//StrokeDescription方法加参数willContinue = false与当前调用方式相同
        .build()

    return dispatchGesture(gestureDescription, null, null)
}

/**
 * 上划
 */
fun AccessibilityService.performScrollForward(nodeInfo: AccessibilityNodeInfo?): Boolean {
    val id = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD.id
    var result = nodeInfo?.performAction(id)
    var parent: AccessibilityNodeInfo? = nodeInfo?.parent
    while (result == false && parent != null) {
        result = parent.performAction(id)
        parent = parent.parent
    }
    return result ?: false
}

fun AccessibilityService.performScrollForward(id: String): Boolean {
    return performScrollForward(findNode(id))
}

/**
 * 下滑
 */
fun AccessibilityService.performScrollBackward(nodeInfo: AccessibilityNodeInfo?): Boolean {
    return nodeInfo?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) ?: false
}

fun AccessibilityService.performScrollBackward(id: String): Boolean {
    return performScrollBackward(findNode(id))
}

/**
 * 想输入框输入文字
 */
fun AccessibilityService.performSetText(nodeInfo: AccessibilityNodeInfo?, text: String): Boolean {
    if (nodeInfo == null) {
        return false
    }
    val className = nodeInfo.className
    if ("android.widget.EditText" == className || "android.widget.TextView" == className) {
        // Android 5.0 版本及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        }
    }
    return false
}

fun AccessibilityService.performSetText(resId: String, text: String): Boolean {
    return performSetText(findNode(resId), text)
}

//遍历子node
fun AccessibilityService.foreachNodes(
    callBack: (node: AccessibilityNodeInfo) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val mWindows = windows
        mWindows?.forEach { window ->
            window.root?.apply {
                foreachNodes(this, callBack = callBack)
            }
        }
    }
}

/**
 * 遍历子node
 * @param root 父节点
 * @param depth 当前深度
 * @param maxDepth 最大深度
 * @param callBack 回调
 */
fun AccessibilityService.foreachNodes(
    root: AccessibilityNodeInfo = rootInActiveWindow,
    depth: Int = 0,
    maxDepth: Int = 50,
    callBack: (node: AccessibilityNodeInfo) -> Unit,
) {
    if (root.childCount == 0) return
    for (index in 0 until root.childCount) {
        root.getChild(index)?.apply { callBack(this) }
    }

    if (depth + 1 > maxDepth) return

    for (index in 0 until root.childCount) {
        root.getChild(index)?.apply {
            if (this.childCount > 0)
                foreachNodes(this, depth + 1, maxDepth, callBack)
        }
    }
}

/**
 * 查找节点，可以通过id，text、desc，className查找
 */
fun AccessibilityService.findNode(
    id: String? = null,
    text: Regex? = null,
    description: Regex? = null,
    className: Regex? = null,
    visible: Boolean? = null,
    selected: Boolean? = null,
    clickable: Boolean? = null,
    enabled: Boolean? = null,
    checked: Boolean? = null,
    scrollable: Boolean? = null,
    root: AccessibilityNodeInfo? = null,
    retry: Int = 0,
    retryDelayed: Long = 500,
    index: Int = 0,
    parentClassName: Regex? = null,
    childCount: Int = -1,
): AccessibilityNodeInfo? =
    findNodes(
        id,
        text,
        description,
        className,
        visible,
        selected,
        clickable,
        enabled,
        checked,
        scrollable,
        retry,
        retryDelayed,
        root,
        parentClassName,
        childCount
    ).let {
        if (it.isEmpty() || index >= it.size) {
            null
        } else {
            it[index]
        }
    }

/**
 * 查找节点，可以通过id，text、desc，className查找
 */
fun AccessibilityService.findNodes(
    id: String? = null,
    text: Regex? = null,
    description: Regex? = null,
    className: Regex? = null,
    visible: Boolean? = null,
    selected: Boolean? = null,
    clickable: Boolean? = null,
    enabled: Boolean? = null,
    checked: Boolean? = null,
    scrollable: Boolean? = null,
    retry: Int = 0,
    retryDelayed: Long = 500,
    root: AccessibilityNodeInfo? = null,
    parentClassName: Regex? = null,
    childCount: Int = -1,
): List<AccessibilityNodeInfo> {
    val nodes = ArrayList<AccessibilityNodeInfo>()

    val callBack: (node: AccessibilityNodeInfo) -> Unit = {
        //perf:一点性能优化吧
        it.match(
            id,
            text,
            description,
            className,
            visible,
            selected,
            clickable,
            enabled,
            checked,
            scrollable,
            parentClassName,
            childCount
        ).apply { if (this) nodes.add(it) }
    }

    //perf:增加重试
    for (i in 0..retry) {
        if (root == null) {
            foreachNodes(callBack)
        } else foreachNodes(root, callBack = callBack)
        if (nodes.size != 0) break
        if (i < retry) try {
            Thread.sleep(retryDelayed)
        } catch (e: Exception) {
            break
        }
    }
    return nodes
}

fun AccessibilityService.clickPermission(pauseControl: PauseControl) {
    val permissionButtons = arrayOf(
        "permission_allow_button",
        "permission_allow_all_button",
        "permission_allow_foreground_only_button",
        "permission_allow_one_time_button"
    )
    permissionButtons.forEach { id ->
        findNode(id)?.apply {
            pauseControl.checkWait()
            performClick(this)
        }
    }
}

fun AccessibilityNodeInfo.match(
    id: String? = null,
    text: Regex? = null,
    description: Regex? = null,
    className: Regex? = null,
    visible: Boolean? = null,
    selected: Boolean? = null,
    clickable: Boolean? = null,
    enabled: Boolean? = null,
    checked: Boolean? = null,
    scrollable: Boolean? = null,
    parentClassName: Regex? = null,
    childCount: Int = -1,
): Boolean {
    return ((id == null) || this.viewIdResourceName != null && (this.viewIdResourceName == id || this.viewIdResourceName.contains(id)))
            && ((text == null) || this.text?.matches(text) == true)
            && ((description == null) || this.contentDescription?.matches(description) == true)
            && ((className == null) || this.className?.matches(className) == true)
            && (visible == null || this.isVisibleToUser == visible)
            && (selected == null || this.isSelected == selected)
            && (clickable == null || this.isClickable == clickable)
            && (enabled == null || this.isEnabled == enabled)
            && (checked == null || this.isChecked == checked)
            && (scrollable == null || this.isScrollable == scrollable)
            && (childCount == -1 || this.childCount == childCount)
            && (parentClassName == null || this.parent?.className?.matches(parentClassName) == true)
}

fun AccessibilityService.findNodeByText(
    regex: Regex,
    visible: Boolean? = null,
): AccessibilityNodeInfo? = findNode(text = regex, visible = visible)

@RequiresApi(Build.VERSION_CODES.N)
fun AccessibilityService.click(x: Float, y: Float): Boolean {
    return click(
        Path().apply { moveTo(x, y) },
        100
    )
}

/*点击屏幕某点*/
@RequiresApi(Build.VERSION_CODES.N)
fun AccessibilityService.click(path: Path, duration: Long = 100): Boolean {
    return gesture(path, duration)
}

@RequiresApi(Build.VERSION_CODES.N)
fun AccessibilityService.click(node: AccessibilityNodeInfo, duration: Long = 100): Boolean {
    val rect = Rect()
    node.getBoundsInScreen(rect)

    val path = Path().apply {
        val x = rect.left + (rect.right - rect.left) / 2f
        val y = rect.top + (rect.bottom - rect.top) / 2f
        moveTo(x, y)
    }
    return click(path, duration)
}

fun AccessibilityService.swipe(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    duration: Long
): Boolean {
    val path = Path().apply {
        moveTo(startX, startY)
        lineTo(endX, endY)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return gesture(path, duration)
    }
    return false
}

fun AccessibilityNodeInfo.getScrollByParent(): AccessibilityNodeInfo? {
    //适配需要滑动的情况
    var parent = this
    while (parent.parent != null) {
        if (parent.parent.isScrollable) {
            return parent.parent
        }
        parent = parent.parent
    }
    return null
}
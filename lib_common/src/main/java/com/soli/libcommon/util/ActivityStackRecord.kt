package com.soli.libcommon.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.soli.libcommon.base.Constant
import java.util.*

/**
 *  Activity 堆栈记录表
 * @author Soli
 * @Time 2020/6/30 17:31
 */
class ActivityStackRecord private constructor(app: Application) :
    Application.ActivityLifecycleCallbacks by noOpDelegate() {

    var resumedActivity: Activity? = null

    companion object {
        /**
         * Actvity存储栈
         */
        private val mActivityStack: Stack<Activity> = Stack()

        @Volatile
        private var instance: ActivityStackRecord? = null

        val stackRecord: ActivityStackRecord
            get() {
                require(instance != null) { "Please ActivityStackRecord.init() first in Application.onCreate" }
                return instance!!
            }

        /**
         *
         */
        fun init(app: Application = Constant.getContext() as Application) {
            instance ?: synchronized(this) {
                instance ?: ActivityStackRecord(app).also {
                    instance = it
                }
            }
        }

    }

    init {
        app.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    val topActivity: Activity?
        get() = if (mActivityStack.size > 0) mActivityStack.lastElement() else null

    /**
     * 获取堆栈中第一个activity
     *
     * @return
     */
    val bottomActivity: Activity?
        get() = if (mActivityStack.size > 0) mActivityStack.firstElement() else null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivityStack.add(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        resumedActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (resumedActivity === activity) {
            resumedActivity = null
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (mActivityStack.contains(activity)) {
            mActivityStack.remove(activity)
        }
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    fun killTopActivity() {
        topActivity?.let { killActivity(it) }
    }

    /**
     * 结束指定的Activity
     */
    fun killActivity(activity: Activity?) {
        if (activity != null) {
            mActivityStack.remove(activity)
            activity.onBackPressed()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    @Synchronized
    fun killActivity(vararg calsses: Class<*>) {

        if (mActivityStack.isEmpty())
            return

        val activities = ArrayList<Activity>()

        for (cls in calsses) {
            for (activity in mActivityStack) {
                if (activity.javaClass == cls) {
                    activities.add(activity)
                }
            }
        }

        for (activity in activities) {
            killActivity(activity)
        }
    }

    /**
     * 结束所有Activity
     */
    fun killAllActivity() {
        if (mActivityStack.isEmpty()) {
            return
        }

        mActivityStack.forEach { it.onBackPressed() }
        mActivityStack.clear()
    }

    /**
     * 结束除了当前的其他所有Activity
     *
     * @param activity
     */
    fun killOthersActivity(activity: Activity?) {
        if (activity == null) {
            return
        }

        mActivityStack.forEach { if (it !== activity) it.onBackPressed() }
        mActivityStack.clear()
        mActivityStack.add(activity)
    }

    /**
     * 判断Activity是否存在
     *
     * @param className
     * @return
     */
    fun existActivity(className: String): Boolean {
        val activity = getActivityByName(className)
        return activity != null && !activity.isFinishing
    }

    /**
     * 根据名字查找Activity
     *
     * @param className
     * @return
     */
    fun getActivityByName(className: String): Activity? =
        mActivityStack.find { it.javaClass.name == className }

    /**
     * 删除并结束掉Activity
     *
     * @param activity
     */
    fun removeActivity(activity: Activity?) {
        activity ?: return

        val index = mActivityStack.indexOf(activity)
        if (index > -1) {
            mActivityStack.removeAt(index)
            activity.onBackPressed()
        }
    }
}

inline val Context.activityStackRecord: ActivityStackRecord
    get() = ActivityStackRecord.stackRecord

inline val Activity.activityStackRecord: ActivityStackRecord
    get() = ActivityStackRecord.stackRecord
package com.soli.libcommon.util

import android.app.Activity
import android.app.Application
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
        private var mActivityStack: Stack<Activity>? = null

        @Volatile
        private var instance: ActivityStackRecord? = null

        fun getInstance(app: Application = Constant.getContext() as Application): ActivityStackRecord =
            instance ?: synchronized(this) {
                instance ?: ActivityStackRecord(app).also {
                    instance = it
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
        get() = if (mActivityStack != null && mActivityStack!!.size > 0) mActivityStack!!.lastElement() else null

    /**
     * 获取堆栈中第一个activity
     *
     * @return
     */
    val bottomActivity: Activity?
        get() = if (mActivityStack != null && mActivityStack!!.size > 0) mActivityStack!!.firstElement() else null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addActivity(activity)
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
        remove(activity)
    }

    /**
     * 添加Activity到堆栈
     */
    private fun addActivity(activity: Activity) {
        if (mActivityStack == null) {
            mActivityStack = Stack()
        }
        mActivityStack!!.add(activity)
    }

    /**
     * 从栈里删除activity
     *
     * @param activity
     */
    private fun remove(activity: Activity?) {
        if (activity != null && mActivityStack != null && mActivityStack!!.contains(activity)) {
            mActivityStack!!.remove(activity)
        }
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    fun killTopActivity() {
        val activity = topActivity
        if (activity != null) {
            killActivity(activity)
        }
    }

    /**
     * 结束指定的Activity
     */
    fun killActivity(activity: Activity?) {
        if (activity != null) {
            mActivityStack!!.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    @Synchronized
    fun killActivity(vararg calsses: Class<*>) {

        if (mActivityStack == null || mActivityStack!!.isEmpty())
            return

        val activities = ArrayList<Activity>()

        for (cls in calsses) {
            for (activity in mActivityStack!!) {
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
        if (mActivityStack == null || mActivityStack!!.isEmpty()) {
            return
        }
        var i = 0
        val size = mActivityStack!!.size
        while (i < size) {
            if (null != mActivityStack!![i]) {
                mActivityStack!![i].finish()
            }
            i++
        }
        mActivityStack!!.clear()
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
        var i = 0
        val size = mActivityStack!!.size
        while (i < size) {
            if (null != mActivityStack!![i] && activity !== mActivityStack!![i]) {
                mActivityStack!![i].finish()
            }
            i++
        }
        mActivityStack!!.clear()
        mActivityStack!!.add(activity)
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
    fun getActivityByName(className: String): Activity? {
        var activity: Activity? = null
        var i = 0
        val size = mActivityStack!!.size
        while (i < size) {
            if (null != mActivityStack!![i]) {
                if (mActivityStack!![i].javaClass.name == className) {
                    activity = mActivityStack!![i]
                }
            }
            i++
        }
        return activity
    }

    /**
     * 删除并结束掉Activity
     *
     * @param activity
     */
    fun removeActivity(activity: Activity?) {

        var pos = -1
        if (activity != null && mActivityStack != null) {
            var i = 0
            val size = mActivityStack!!.size
            while (i < size) {
                if (null != mActivityStack!![i]) {
                    if (activity === mActivityStack!![i]) {
                        pos = i
                        activity.finish()
                    }
                }
                i++
            }
            if (pos != -1) {
                mActivityStack!!.removeAt(pos)
            }
        }
    }


}

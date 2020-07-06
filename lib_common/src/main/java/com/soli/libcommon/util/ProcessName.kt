package com.soli.libcommon.util

import android.app.ActivityManager
import android.content.Context
import com.soli.libcommon.base.Constant

/**
 *
 * @author Soli
 * @Time 2019-04-26 17:24
 */
object ProcessName {

    //主进程
    val MainProcess by lazy { Constant.getContext().packageName }

    //易信聊天进程
    val NimCoreProcess = "$MainProcess:core"

    val JpusCoreProcess = "$MainProcess:pushcore"


    /**
     * 获取当前进程名字
     *
     * @param ctx
     * @return
     */
    fun getCurrentProcessName(ctx: Context = Constant.getContext()): String {
        val processId = android.os.Process.myPid()
        val manager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (info in manager.runningAppProcesses) {
            if (processId == info.pid) {
                return info.processName
            }
        }

        return ""
    }
}
package com.soli.libcommon.util

import android.app.ActivityManager
import android.content.Context
import com.soli.libcommon.base.Constant
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 *
 * @author Soli
 * @Time 2019-04-26 17:24
 */
object ProcessName {

    //主进程
    val MainProcess by lazy { Constant.context.packageName }

    //易信聊天进程
    val NimCoreProcess = "$MainProcess:core"

    val JpusCoreProcess = "$MainProcess:pushcore"


    /**
     * 获取当前进程名字
     *
     * @param ctx
     * @return
     */
    fun getCurrentProcessName(ctx: Context = Constant.context): String {
        val processId = android.os.Process.myPid()
        val manager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (info in manager.runningAppProcesses) {
            if (processId == info.pid) {
                return info.processName
            }
        }

        return ""
    }

    /**
     * 另外一种获取进程信息
     */
    fun getProcessName(): String =
        try {
            val file = File("/proc/self/cmdline")
            val bufferReader = BufferedReader(FileReader(file))
            val processName = bufferReader.readLine().trim()
            bufferReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

}
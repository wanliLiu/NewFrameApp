package com.soli.newframeapp.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.soli.libcommon.util.MLog
import java.io.File
import java.net.NetworkInterface


/**
 *
 * @Author:        zhazha
 * @CreateDate:    2021/4/8 5:08 下午
 * @Description:   app的工具类
 */
object AppUtils {

    /**
     * 根据包名启动app
     */
    fun startApp(context: Context, packageName: String): Boolean {
        return context.applicationContext.packageManager.getLaunchIntentForPackage(packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                context.startActivity(this)
            } != null
    }

    fun appInstalled(context: Context, packageName: String): Boolean {
        return context.applicationContext.packageManager.getLaunchIntentForPackage(packageName) != null
    }

    /**
     * 获取 app version name
     */
    fun getAppVersionName(context: Context, packageName: String): String? {
        try {
            return context.applicationContext.packageManager.getPackageInfo(
                packageName,
                0
            ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取 app version code
     */
    fun getAppVersionCode(packageName: String, context: Context): Long {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.applicationContext.packageManager.getPackageInfo(
                    packageName,
                    0
                ).longVersionCode
            } else {
                context.applicationContext.packageManager.getPackageInfo(
                    packageName,
                    0
                ).versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return 0L
    }

    fun getRequestedPermissions(context: Context, packageName: String): Array<String>? {
        try {
            return context.applicationContext.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            ).requestedPermissions
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 获取 app name
     */
    fun getAppName(packageName: String, context: Context): String {
        try {
            return context.applicationContext.packageManager.getApplicationLabel(
                context.applicationContext.packageManager.getApplicationInfo(
                    packageName,
                    0
                )
            ).toString()
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return ""
    }

    fun installApk(context: Context, apk: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val file = File(apk)
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                file
            )
            val resInfoList: List<ResolveInfo> = context.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName: String = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        } else {
            uri = Uri.fromFile(file)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * 获取顶层app的activity索引，1开始
     */
    fun getTopActivityIndex(context: Context): Int {
        val manager =
            context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningTasks(1)[0].numActivities
    }

    /**
     * @return 当前activity
     */
    fun getTopActivity(context: Context): String {
        val manager =
            context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningTasks(1)[0].topActivity?.className ?: ""
    }

    /**
     * @return 当前app包名
     */
    fun getTopApp(context: Context): String? {
        val manager =
            context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)[0]
        return runningTaskInfo.baseActivity
            ?.packageName
            ?.let {
                if (it == "com.android.packageinstaller")
                    runningTaskInfo.topActivity?.packageName
                else it
            }
    }

    /**
     * @param packageName 对应的包名
     * @return packageName对应的RunningTaskInfo
     */
    fun getRunningTaskInfo(
        context: Context,
        packageName: String
    ): ActivityManager.RunningTaskInfo? {
        NetworkInterface.getNetworkInterfaces()
        return context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE)
            .let { it as ActivityManager }
            .getRunningTasks(100)
            .firstOrNull { it.baseActivity?.packageName == packageName }
    }

    /**
     * 返回应用
     */
    fun backToApp(context: Context, packageName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (task in activityManager.appTasks) {
                if (task.taskInfo.baseIntent.component?.packageName.equals(packageName)) {
                    task.moveToFront()
                    return
                }
            }
        }
        startApp(context, packageName)
    }

    /**
     * 获取 app version code
     */
    fun getPrimaryCpuAbi(context: Context, packageName: String): String? {
        return try {
            val applicationInfo = context.applicationContext
                .packageManager
                .getApplicationInfo(packageName, 0)

            applicationInfo.javaClass
                .getDeclaredField("primaryCpuAbi")
                .apply { isAccessible = true }
                .get(applicationInfo)
                ?.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getNativeLibraryDir(context: Context, packageName: String): String? {
        return try {
            context.applicationContext
                .packageManager
                .getApplicationInfo(packageName, 0)
                .nativeLibraryDir
        } catch (e: Exception) {
            null
        }
    }

    fun getFlavor(context: Context): String? {
        try {
            val clazz = Class.forName(context.packageName + ".BuildConfig")
            val field = clazz.getField("FLAVOR")
            return field.get(null)?.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
package com.soli.libcommon.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.lang.Exception

/**
 *
 * @author Soli
 * @Time 2019-10-28 14:08
 */
object DirectoryUtl {

    fun getSystemDir(ctx: Context, type: String? = null): File = ctx.getExternalFilesDir(type)!!

    fun getAppRootDir(ctx: Context, isIn: Boolean = true): File {
        var dir: File? = null

        try {
            if (isIn) {
                dir = ctx.cacheDir
            } else {
                dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getSystemDir(ctx) else
                    File(Environment.getExternalStorageDirectory(), "frame")
                if (!dir.exists())
                    dir.mkdirs()
            }
        } catch (e: Exception) {
        }

        if (dir == null || !dir.exists()) {
            dir = ctx.cacheDir!!
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }

        return dir
    }

}
package com.soli.libcommon.util

import java.io.Closeable
import java.io.IOException

/**
 * 关闭相关工具类
 */
object CloseUtils {

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    fun closeIO(vararg closeables: Any?) {
        for (closeable in closeables) {
            try {
                (closeable as? Closeable)?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeable
     */
    fun closeIOQuietly(vararg closeables: Any) {
        for (closeable in closeables) {
            try {
                (closeable as? Closeable)?.close()
            } catch (ignored: IOException) {
            }
        }
    }
}

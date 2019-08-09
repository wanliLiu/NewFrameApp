package com.soli.libcommon.net.download

import okhttp3.ResponseBody
import okio.*

/**
 * @author Soli
 * @Time 18-6-7 上午10:40
 */
class ProgressResponseBody
private constructor(
    val responseBody: ResponseBody,
    val progressListener: (progress: Int, bytesRead: Long, updates: Long, contentLength: Long, done: Boolean) -> Unit
) : ResponseBody() {

    companion object {
        /**
         * 故意这样些写的，kotlin的单列实现
         */
        fun create(
            responseBody: ResponseBody,
            progressListener: (progress: Int, bytesRead: Long, updates: Long, contentLength: Long, done: Boolean) -> Unit
        ) = ProgressResponseBody(responseBody, progressListener)
    }

    private var bufferedSource: BufferedSource? = null

    override fun contentLength() = responseBody.contentLength()

    override fun contentType() = responseBody.contentType()

    override fun source(): BufferedSource {

        if (bufferedSource == null)
            bufferedSource = source(responseBody.source()).buffer()

        return bufferedSource!!
    }

    /**
     * 从数据流中截取，来算
     */
    private fun source(source: Source) =
        object : ForwardingSource(source) {
            private var progress = -1
            private var totalBytesRead = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                val contentLength = contentLength()

                if (contentLength > 0) {
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                    val temp = ((100 * totalBytesRead) / contentLength).toInt()
                    if (progress != temp) {
                        progress = temp
                        progressListener.invoke(progress, totalBytesRead, byteCount, contentLength, bytesRead == -1L)
                    }
                }

                return bytesRead
            }
        }
}
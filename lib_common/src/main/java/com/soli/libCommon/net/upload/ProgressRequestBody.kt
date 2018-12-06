package com.soli.libCommon.net.upload

import com.soli.libCommon.net.download.FileProgressListener
import com.soli.libCommon.util.RxJavaUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/*
 * @author soli
 * @Time 2018/12/5 21:05
 */
internal class ProgressRequestBody(
    private val mDelegate: RequestBody,
    private val progressListener: FileProgressListener
) : RequestBody() {
    private var mBufferedSink: BufferedSink? = null

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mDelegate.contentLength()
    }

    override fun contentType(): MediaType? {
        return mDelegate.contentType()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (sink is Buffer) {
            // Log Interceptor
            mDelegate.writeTo(sink)
            return
        }
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(wrapSink(sink))
        }
        mDelegate.writeTo(mBufferedSink!!)
        mBufferedSink!!.flush()
    }

    private fun wrapSink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            private var progress = -1
            private var totalBytesSend = 0L
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)

                val contentLength = contentLength()

                if (contentLength > 0) {
                    totalBytesSend += if (byteCount != -1L) byteCount else 0

                    val temp = ((100 * totalBytesSend) / contentLength).toInt()
                    if (progress != temp) {
                        progress = temp
                        RxJavaUtil.runOnUiThread{
                            progressListener.progress(progress, totalBytesSend, contentLength, byteCount == -1L)
                        }
                    }
                }
            }
        }
    }
}

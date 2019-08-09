package com.soli.libcommon.net.upload

import com.soli.libcommon.net.download.FileProgressListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/*
 * @author soli
 * @Time 2018/12/5 21:05
 */
class ProgressRequestBody(
    private val mDelegate: RequestBody,
    private val progressListener: FileProgressListener
) : RequestBody() {

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
//        if (mBufferedSink == null) {
        val mBufferedSink = wrapSink(sink).buffer()
//        }
        mDelegate.writeTo(mBufferedSink)
        mBufferedSink.flush()
    }

    private fun wrapSink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            private var progress = -1
            private var totalBytesSend = 0L
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)

                val contentLength = contentLength()

                val update = if (byteCount != -1L) byteCount else 0
                totalBytesSend += update

                val temp = ((100 * totalBytesSend) / contentLength).toInt()
                if (progress != temp) {
                    progress = temp
//                        RxJavaUtil.runOnUiThread {
                    progressListener.progress(
                        progress,
                        totalBytesSend,
                        update,
                        contentLength,
                        byteCount == -1L
                    )
//                        }
                }
            }
        }
    }
}

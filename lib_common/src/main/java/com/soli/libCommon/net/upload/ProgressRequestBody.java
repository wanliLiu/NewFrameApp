package com.soli.libCommon.net.upload;

import io.reactivex.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.*;

import java.io.IOException;

/*
 * @author soli
 * @Time 2018/12/5 21:05
 */
class ProgressRequestBody extends RequestBody {

    private RequestBody mDelegate;
    private Emitter<UploadProgressInfo> mEmitter;
    private UploadProgressInfo mProgressInfo;
    private BufferedSink mBufferedSink;

    ProgressRequestBody(RequestBody delegate, Emitter<UploadProgressInfo> emitter,
                        UploadProgressInfo info) {
        mDelegate = delegate;
        mEmitter = emitter;
        mProgressInfo = info;
    }

    @Override
    public long contentLength() throws IOException {
        return mDelegate.contentLength();
    }

    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (sink instanceof Buffer) {
            // Log Interceptor
            mDelegate.writeTo(sink);
            return;
        }
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(wrapSink(sink));
        }
        mDelegate.writeTo(mBufferedSink);
        mBufferedSink.flush();
    }

    private Sink wrapSink(Sink sink) {
        return new ForwardingSink(sink) {

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (mProgressInfo.total == 0) {
                    mProgressInfo.total = contentLength();
                }
                mProgressInfo.current += byteCount;
                mEmitter.onNext(mProgressInfo);
            }
        };
    }
}

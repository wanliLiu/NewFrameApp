package com.soli.libcommon.net.download;

/**
 * @author Soli
 * @Time 18-6-7 下午3:42
 */
public interface FileProgressListener {
    /**
     * @param progress
     * @param bytes
     * @param fileSize
     * @param isDone
     */
    void progress(int progress, long bytes, long fileSize, boolean isDone);
}

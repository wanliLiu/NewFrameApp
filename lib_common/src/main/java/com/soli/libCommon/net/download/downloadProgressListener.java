package com.soli.libCommon.net.download;

/**
 * @author Soli
 * @Time 18-6-7 下午3:42
 */
public interface downloadProgressListener {
    /**
     * @param progress
     * @param bytesRead
     * @param fileSize
     * @param isDone
     */
    void progress(int progress, long bytesRead, long fileSize, boolean isDone);
}

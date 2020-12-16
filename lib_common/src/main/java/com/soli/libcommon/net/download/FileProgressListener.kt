package com.soli.libcommon.net.download;

/**
 * @author Soli
 * @Time 18-6-7 下午3:42
 */
public interface FileProgressListener {
    /**
     * @param progress    当个文件进度条
     * @param bytes       已经操作过数量
     * @param updateBytes 实时更新的数量
     * @param fileSize    文件的总大小
     * @param isDone      是否完成
     */
    void progress(int progress, long bytes, long updateBytes, long fileSize, boolean isDone);
}

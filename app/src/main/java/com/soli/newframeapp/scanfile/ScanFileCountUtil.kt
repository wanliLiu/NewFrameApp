package com.soli.newframeapp.scanfile

import android.os.Handler
import android.os.Message
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Soli
 * @Time 2020/10/22 11:01
 */
class ScanFileCountUtil(
    private val mFilePath: String,
    private val mCategorySuffix: MutableMap<String, Set<String>>,
    private val mHandler: Handler
) {
    //最终的统计结果
    private var mCountResult =
        ConcurrentHashMap<String, ConcurrentHashMap<String, MutableSet<String>>>()

    //用于存储文件目录便于层次遍历
    private var mFileConcurrentLinkedQueue: ConcurrentLinkedQueue<File> = ConcurrentLinkedQueue()

    private fun File.filterHidderFile() = this.listFiles { _, name -> !name.startsWith(".") }

    /**
     *
     */
    fun startScanFile() {
        val file = File(mFilePath)

        //非目录或者目录不存在直接返回
        if (!file.exists() || file.isFile) {
            return
        }

        //临时存储任务,便于后面全部投递到线程池
        val runnableList = mutableListOf<Runnable>()
        val files = file.filterHidderFile()
        for (file in files) {
            if (file.isDirectory) {
                //把目录添加进队列
                mFileConcurrentLinkedQueue.offer(file)
                //创建的线程的数目是根目录下文件夹的数目
                val runnable = Runnable { countFile() }
                runnableList.add(runnable)
            } else {
                //找到该文件所属的类别
                for ((key, value) in mCategorySuffix) {
                    //获取文件后缀
//                    val suffix = FileUtil.getFileExtension(file.absolutePath)
                    val suffix = file.name.substring(file.name.indexOf(".") + 1).toLowerCase()
                    //找到了
                    if (value.contains(suffix)) {
                        var temp = mCountResult[key]
                        if (temp == null) {
                            temp = ConcurrentHashMap()
                            mCountResult[key] = temp
                        }
                        val dirName = file.parentFile.name
                        var dir = temp[dirName]
                        if (dir == null) {
                            dir = HashSet()
                            temp[dirName] = dir
                        }
                        dir.add(file.absolutePath)
                        break
                    }
                }
            }
        }

        //固定数目线程池(最大线程数目为cpu核心数,多余线程放在等待队列中)
        val executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        for (runnable in runnableList) {
            executorService.submit(runnable)
        }
        //不允许再添加线程
        executorService.shutdown()
        //等待线程池中的所有线程运行完成
        while (true) {
            if (executorService.isTerminated) {
                break
            }
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        //传递统计数据给UI界面
        val msg = Message.obtain()
        msg.obj = mCountResult
        mHandler.sendMessage(msg)
    }

    /**
     * 统计各类型文件数目
     */
    private fun countFile() {
        //对目录进行层次遍历
        while (!mFileConcurrentLinkedQueue.isEmpty()) {
            //队头出队列
            val tmpFile = mFileConcurrentLinkedQueue.poll().filterHidderFile()
            for (f in tmpFile) {
                if (f.isDirectory) {
                    //把目录添加进队列
                    mFileConcurrentLinkedQueue.offer(f)
                } else {
                    //找到该文件所属的类别
                    for ((key, value) in mCategorySuffix) {
                        //获取文件后缀
//                        val suffix = FileUtil.getFileExtension(f.absolutePath)
                        val suffix = f.name.substring(f.name.indexOf(".") + 1).toLowerCase()
                        //找到了
                        if (value.contains(suffix)) {
                            var temp = mCountResult[key]
                            if (temp == null) {
                                temp = ConcurrentHashMap()
                                mCountResult[key] = temp
                            }
                            val dirName = f.parentFile.name
                            var dir = temp[dirName]
                            if (dir == null) {
                                dir = HashSet()
                                temp[dirName] = dir
                            }
                            dir.add(f.absolutePath)
                            break
                        }
                    }
                }
            }
        }
    }
}
package com.soli.newframeapp.scanfile

import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.libcommon.util.clickView
import com.soli.newframeapp.R
import kotlinx.android.synthetic.main.fragment_scan_file.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * @author Soli
 * @Time 2020/10/22 10:55
 */
class ScanFileFagment : BaseToolbarFragment() {

    private var start = 0L
    private val mhander = Handler(Looper.getMainLooper()) {
        val countres = it.obj as Map<String, Int>
        scanResult.text = "扫描结束\n"
        scanResult.append("扫描时间：${System.currentTimeMillis() - start}ms\n")
        scanResult.append(countres.toString())
        true
    }

    override fun getContentView(): Int = R.layout.fragment_scan_file

    override fun initView() {
        setTitle("扫描本地文件")
    }

    override fun initListener() {
        scanFileView.clickView {
            scanResult.text = "扫描中......"
            start = System.currentTimeMillis()
            scanFile()
        }
    }

    override fun initData() {
    }


    private fun scanFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return
        }
        val path: String = Environment.getExternalStorageDirectory().getAbsolutePath()
        val CATEGORY_SUFFIX: MutableMap<String, Set<String?>> = HashMap()
        var set: MutableSet<String?> = HashSet()
        set.add("mp4")
        set.add("avi")
        set.add("wmv")
        set.add("flv")
        CATEGORY_SUFFIX["video"] = set
        set.add("txt")
        set.add("pdf")
        set.add("doc")
        set.add("docx")
        set.add("xls")
        set.add("xlsx")
        CATEGORY_SUFFIX["document"] = set
        set = HashSet()
        set.add("jpg")
        set.add("jpeg")
        set.add("png")
        set.add("bmp")
        set.add("gif")
        CATEGORY_SUFFIX["picture"] = set
        set = HashSet()
        set.add("mp3")
        set.add("ogg")
        CATEGORY_SUFFIX["music"] = set
        set = HashSet()
        set.add("apk")
        CATEGORY_SUFFIX["apk"] = set
        set = HashSet()
        set.add("zip")
        set.add("rar")
        set.add("7z")
        CATEGORY_SUFFIX["zip"] = set

        //单一线程线程池
        val singleExecutorService: ExecutorService = Executors.newSingleThreadExecutor()
        singleExecutorService.submit(Runnable { //构建对象
            val scanFileCountUtil = ScanFileCountUtil.Builder(mhander)
                .setFilePath(path)
                .setCategorySuffix(CATEGORY_SUFFIX)
                .create()
            scanFileCountUtil.scanCountFile()
        })
    }
}
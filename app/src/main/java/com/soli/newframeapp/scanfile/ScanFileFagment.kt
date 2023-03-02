package com.soli.newframeapp.scanfile

import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.soli.libcommon.base.BaseToolbarFragment
import com.soli.libcommon.util.clickView
import com.soli.newframeapp.databinding.FragmentScanFileBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * @author Soli
 * @Time 2020/10/22 10:55
 */
class ScanFileFagment : BaseToolbarFragment<FragmentScanFileBinding>() {

    private var start = 0L
    private val mhander = Handler(Looper.getMainLooper()) {
        binding.scanResult.text = "扫描结束\n"
        binding.scanResult.append("扫描时间：${System.currentTimeMillis() - start}ms\n")
        binding.scanResult.append(it.obj.toString())
        true
    }

    override fun initView() {
        setTitle("扫描本地文件")
    }

    override fun initListener() {
        binding.scanFileView.clickView {
            binding.scanResult.text = "扫描中......"
            start = System.currentTimeMillis()
            scanFile()
        }
    }

    override fun initData() {
    }


    private fun scanFile() {
        start = System.currentTimeMillis()
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return
        }
        val path: String =
            Environment.getExternalStorageDirectory().absolutePath // "/storage/emulated/0/DCIM/Camera/"
        val CATEGORY_SUFFIX: MutableMap<String, Set<String>> = HashMap()
        var set: MutableSet<String> = HashSet()
        set.add("mp4")
        set.add("avi")
        set.add("wmv")
        set.add("flv")
        CATEGORY_SUFFIX["Videos"] = set
        set = HashSet()
        set.add("txt")
        set.add("pdf")
        set.add("doc")
        set.add("docx")
        set.add("xls")
        set.add("xlsx")
        CATEGORY_SUFFIX["Docs"] = set
        set = HashSet()
        set.add("jpg")
        set.add("jpeg")
        set.add("png")
        set.add("bmp")
        set.add("gif")
        CATEGORY_SUFFIX["Images"] = set
        set = HashSet()
        set.add("mp3")
        set.add("ogg")
        CATEGORY_SUFFIX["Music"] = set
        set = HashSet()
        set.add("apk")
        CATEGORY_SUFFIX["Apk"] = set
        set = HashSet()
        set.add("zip")
        set.add("rar")
        set.add("7z")
        CATEGORY_SUFFIX["Zip"] = set

        //单一线程线程池
        val singleExecutorService: ExecutorService = Executors.newSingleThreadExecutor()
        singleExecutorService.submit { //构建对象
            ScanFileCountUtil(path, CATEGORY_SUFFIX, mhander).startScanFile()
        }
    }
}
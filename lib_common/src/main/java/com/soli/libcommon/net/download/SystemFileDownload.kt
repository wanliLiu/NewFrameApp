package com.soli.libcommon.net.download

import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils
import com.facebook.common.util.UriUtil
import com.soli.libcommon.net.cookie.https.HttpsUtils
import com.soli.libcommon.util.FileUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_MOVED_TEMP
import java.net.HttpURLConnection.HTTP_SEE_OTHER
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 *  用系统的HttpsURLConnection来下载，简单弄了下
 * @author Soli
 * @Time 2019-06-18 14:16
 */
class SystemFileDownload(
    private val url: String?,
    private val localFile: File,
    private val onResult: (isSuccess: Boolean, msg: String?) -> Unit,
    private val progress: (progress: Int) -> Unit
) : AsyncTask<String, Int, String>() {

    private var errormsg = ""

    var isNeedToStop = false

    override fun onPreExecute() {
        errormsg = ""
    }

    /**
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun openConnection(mPath: String): HttpURLConnection? {
        var connection: HttpURLConnection
        var redirected: Boolean
        var redirectCount = 0

        var path = mPath
        do {
            val url = URL(path)
            connection = if (path.startsWith("https")) {
                val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
                HttpsURLConnection.setDefaultSSLSocketFactory(sslParams.sSLSocketFactory)

                val conn = url.openConnection() as HttpsURLConnection
                conn.setHostnameVerifier { _, _ -> true }
                conn
            } else
                url.openConnection() as HttpURLConnection

            val code = connection.responseCode
            redirected =
                code == HttpURLConnection.HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP || code == HTTP_SEE_OTHER
            if (redirected) {
                //重定向
                path = connection.getHeaderField("Location")
                redirectCount++
                connection.disconnect()
            }
            require(redirectCount <= 5) { "Too many redirects: $redirectCount" }
        } while (redirected && !isNeedToStop)

        return if (isNeedToStop) null else connection
    }

    override fun doInBackground(vararg params: String?): String? {
        try {
            if (isNeedToStop) {
                errormsg = "中断文件下载"
                return null
            }

            errormsg = ""

            if (!TextUtils.isEmpty(url) && url!!.startsWith("http")) {
                if (localFile.exists()) {
//                    LogUtils.e("不需要下载", url)
                    return localFile.absolutePath
                } else {
//                    LogUtils.e("开始下载", url)

                    if (isNeedToStop) {
                        errormsg = "中断文件下载"
                        return null
                    }

                    val connection = openConnection(url)

                    if (connection == null || isNeedToStop) {
                        errormsg = "中断文件下载"
                        return null
                    }

                    val responseCode = connection.responseCode
                    if (responseCode in 200..299) {

                        val bis = BufferedInputStream(connection.inputStream)

                        val buffer = ByteArray(4096)
                        val stream = FileOutputStream(localFile)

                        val fileSize = connection.contentLength
                        var lengths = 0
                        var mlastrogress = 0
                        var mProgress: Int

                        var bytesRead = bis.read(buffer)
                        while (bytesRead != -1) {

                            if (isNeedToStop) break

                            //不是暂停命令，不是取消命令，数据没写入完，就执行下面代码
                            stream.write(buffer, 0, bytesRead)
                            //获取当前进度值
                            lengths += bytesRead
                            mProgress = (lengths * 1.0f / fileSize * 1.0f * 100).toInt()
                            if (mlastrogress != mProgress) {
                                //通过间隔时间  与 上一次的进度和当前进度是否一样，不一样再通知handle，节省资源
                                mlastrogress = mProgress
                                publishProgress(mlastrogress)
                            }
                            bytesRead = bis.read(buffer)
                        }
                        stream.close()
                        bis.close()

                        return if (isNeedToStop) {
                            if (localFile.exists())
                                localFile.delete()
                            errormsg = "中断文件下载"
                            null
                        } else
                            localFile.absolutePath
                    }
                }
            } else if (UriUtil.isLocalFileUri(Uri.parse(url))) {
                return url?.replace("file://", "") ?: ""
            }
        } catch (e: Exception) {
            errormsg = "文件下载出错-->$e + ---> ${e.message}"
            e.printStackTrace()
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Int?) {
        if (!isNeedToStop)
            progress(values[0] ?: 0)
    }

    override fun onPostExecute(result: String?) {
        val isSuccess = !TextUtils.isEmpty(result)
        onResult(
            isSuccess, if (isSuccess) localFile.absolutePath else {
                FileUtil.delete(localFile.absolutePath)
                errormsg
            }
        )
    }

    /**
     * 开始下载
     */
    fun startDownload() {
        this.execute(null)
    }

}
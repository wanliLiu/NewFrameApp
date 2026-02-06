package com.soli.newframeapp.download

import android.annotation.TargetApi
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.soli.libcommon.util.ShellUtils
import com.soli.newframeapp.R
import com.soli.newframeapp.util.InstallUtil
import java.io.File


/**
 * 用系统的DownloadManager来下载
 * @author Soli
 * @Time 2018/8/29 13:36
 */
class DownloadService : Service() {

    private var mDownloadManager: DownloadManager? = null
    private val mBinder = DownloadBinder()
    private var mApkPaths: androidx.collection.LongSparseArray<String>? = null
    private var mReceiver: DownloadFinishReceiver? = null

    override fun onCreate() {
        super.onCreate()

        mDownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        mApkPaths = androidx.collection.LongSparseArray()
        //注册下载完成的广播
        mReceiver = DownloadFinishReceiver()
        ContextCompat.registerReceiver( applicationContext, mReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_EXPORTED)

        //Android 8.0 以上  服务需要特殊处理一下，注册自己的notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)//取消注册广播接收者
        super.onDestroy()

        stopForeground(true)
    }

    inner class DownloadBinder : Binder() {
        /**
         * 下载
         * @param url 下载的url
         * @param savePath 存储的路径
         */
        fun startDownload(url: String, savePath: File): Long {
            //点击下载
            //使用DownLoadManager来下载
            val request = DownloadManager.Request(Uri.parse(url))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            //将文件下载到自己的Download文件夹下,必须是External的
            request.setDestinationUri(Uri.fromFile(savePath))
            //添加请求 开始下载
            val downloadId = mDownloadManager!!.enqueue(request)
            mApkPaths!!.put(downloadId, savePath.absolutePath)
            return downloadId
        }

        /**
         * 获取进度信息
         * @param downloadId 要获取下载的id
         * @return 进度信息 max-100
         */
        fun getProgress(downloadId: Long): Int {
            //查询进度
            val query = DownloadManager.Query().setFilterById(downloadId)
            var cursor: Cursor? = null
            var progress = 0
            try {
                cursor = mDownloadManager!!.query(query)//获得游标
                if (cursor.moveToFirst()) {
                    //当前的下载量
                    val downloadSoFar = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    //文件总大小
                    val totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    progress = (downloadSoFar * 1.0f / totalBytes * 100).toInt()
                }
            } finally {
                cursor?.close()
            }

            return progress
        }

        /**
         *
         */
        fun stopEverthing(){
            stopSelf()
        }
    }

    //下载完成的广播
    private inner class DownloadFinishReceiver : BroadcastReceiver() {

        @Override
        override fun onReceive(context: Context, intent: Intent) {
            //下载完成的广播接收者
            val completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val filePath = mApkPaths!!.get(completeDownloadId)
            if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                if (filePath!!.endsWith(".apk")) {
                    val result = ShellUtils.execCmd("chmod 777 $filePath", false).result == 0
                    InstallUtil.install(context, File(filePath))
                }
            } else {
                Log.e("DownloadFinishReceiver", "apkPath is null")
            }
        }
    }

    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 通知渠道的id
        val id = "my_channel_01"
        // 用户可以看到的通知渠道的名字.
        val name = "名字"
        //用户可以看到的通知渠道的描述
        val description = "描述"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(id, name, importance)
        //配置通知渠道的属性
        mChannel.description = description
        //设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(true)
        mChannel.lightColor = Color.RED
        //设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        //最后在notificationmanager中创建该通知渠道 //
        mNotificationManager.createNotificationChannel(mChannel)

        // 为该通知设置一个id
        val notifyID = 1
        // Create a notification and set the notification channel.
        val notification = Notification.Builder(this,mChannel.id)
                .setContentTitle("服务").setContentText("启动服务来下载apk进行安装")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        startForeground(notifyID, notification)
    }
}
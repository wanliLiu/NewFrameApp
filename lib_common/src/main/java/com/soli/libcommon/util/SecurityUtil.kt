package com.soli.libcommon.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.soli.libcommon.R
import com.soli.libcommon.base.Constant
import com.soli.libcommon.net.ApiHelper

/**
 *  代理 和vpn处理
 * @author Soli
 * @Time 2019-10-16 14:19
 */
object SecurityUtil {

    //能够抓包，默认不行
    @JvmStatic
    var needCapturePacket = false
    var haveShowVpnDialog = false

    /**
     *
     */
    fun doSomething(exit: Boolean = false) {
        needCapturePacket = !needCapturePacket
        if (exit)
            needCapturePacket = false
        ApiHelper.resetApiClient()
    }

    /***
     * true  有代理  false没有代理
     */
    private fun checkIfHaveProxy(): Boolean {
        val proxyHost = System.getProperty("http.proxyHost", "")
        val proxyPort = System.getProperty("http.proxyPort", "-1")
        val proxyHost_s = System.getProperty("https.proxyHost", "")
        val proxyPort_s = System.getProperty("https.proxyPort", "-1")
        MLog.d("手机代理情况：http->$proxyHost:$proxyPort   https->$proxyHost_s:$proxyPort_s")
        return !(TextUtils.isEmpty(proxyHost) && proxyPort == "-1" && TextUtils.isEmpty(proxyHost_s) && proxyPort_s == "-1")
    }

    /**
     * 移除网络代理，这个必须在网络请求前设置，另外一个是直接通过Okhttp来设置
     */
    private fun removeProxyBeforeRequest() {
        System.getProperties().remove("http.proxyHost")
        System.getProperties().remove("http.proxyPort")
        System.getProperties().remove("https.proxyHost")
        System.getProperties().remove("https.proxyPort")
    }

    /**
     * 打开网络设置界面
     *
     */
    private fun openVPNSettings() {
        try {
            Constant.context.startActivity(
                Intent(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Settings.ACTION_VPN_SETTINGS else "android.net.vpn.SETTINGS").setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *
     */
    private fun hasVpnTransport(
        network: Network?,
        connectivityManager: ConnectivityManager?
    ): Boolean {
        network ?: return false
        connectivityManager ?: return false

        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }

    /**
     *检测是否有vpn
     */
    private fun checkIfHaveVPN(): Boolean {
        val connectivityManager =
            Constant.context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasVpnTransport(connectivityManager.activeNetwork, connectivityManager)
        } else {
            val networks = connectivityManager.allNetworks ?: return false
            for (net in networks) {
                if (hasVpnTransport(net, connectivityManager)) {
                    return true
                }
            }
            false
        }
    }

    private fun showVpnCloseDialog() {
        if (!haveShowVpnDialog) {
            try {
                val stackRecord = ActivityStackRecord.stackRecord.topActivity ?: return
                val dialog = AlertDialog.Builder(stackRecord)
                dialog.setMessage(R.string.str_security_des)
                dialog.setCancelable(false)
                dialog.setOnDismissListener { haveShowVpnDialog = false }
                dialog.setPositiveButton(R.string.str_security_close) { _, _ ->
                    haveShowVpnDialog = false
                    openVPNSettings()
                }
                dialog.show()
                haveShowVpnDialog = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 是否能够抓包
     */
    @JvmStatic
    fun dealNetSecurityCheck(): Boolean {
        var haveExce = false
        if (!needCapturePacket) {
            //有代理移除代理
//            if (checkIfHaveProxy()) {
//                removeProxyBeforeRequest()
//            }

            //只处理不能抓包的情况
            if (checkIfHaveVPN()) {
                haveExce = true
                showVpnCloseDialog()
            }
        }

        return haveExce
    }

}
package com.soya.launcher.ext

import android.net.wifi.WifiManager
import android.content.Context
import android.os.Build
import com.shudong.lib_base.ext.appContext

fun getWifiName(): String {
    val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    return getNoDoubleQuotationSSID(wifiInfo.ssid)
}

fun isGreaterThanLollipop(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
}

private fun getNoDoubleQuotationSSID(ssid: String): String {

        //获取Android版本号
        var ssid = ssid
        val deviceVersion = Build.VERSION.SDK_INT
        if (deviceVersion >= 17) {
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length - 1)
            }
        }
        return ssid
    }
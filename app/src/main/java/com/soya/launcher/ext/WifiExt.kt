package com.soya.launcher.ext

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.os.Build
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e

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

@SuppressLint("MissingPermission")
fun WifiManager.disconnectCurrentWifi() {
    // 获取当前连接的网络
    val currentNetwork = connectionInfo

    // 如果当前有连接的网络，且网络 ID 不为 -1
    if (currentNetwork != null && currentNetwork.networkId != -1) {
        // 断开当前连接的 Wi-Fi 网络
        //disconnect()
        disableNetwork(currentNetwork.networkId)
    }
}

@SuppressLint("MissingPermission")
fun WifiManager.connectToSavedWiFi( ssid: String): Boolean {

    // 获取保存的 Wi-Fi 配置列表
    val configuredNetworks = this.configuredNetworks

    // 查找指定 SSID 的 Wi-Fi 配置
    val wifiConfig = configuredNetworks.find { it.SSID == "\"$ssid\"" }

    return if (wifiConfig != null) {
        // 启用该网络并连接
        this.enableNetwork(wifiConfig.networkId, true)
        //this.reconnect()
        true
    } else {
        false
    }
}

// 扩展函数：连接到指定的 Wi-Fi 网络（包括处理免费和非免费网络）
@SuppressLint("MissingPermission")
fun WifiManager.connectToWiFi(ssid: String, password: String? = null): Boolean {
    val wifiConfig = WifiConfiguration().apply {
        SSID = "\"$ssid\""

        // 如果密码为空，认为是免费网络
        if (password.isNullOrEmpty()) {
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE) // 设置为开放网络
        } else {
            preSharedKey = "\"$password\""
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK) // 设置为 WPA2 PSK 网络
        }
    }

    // 先查找是否已经有相同的配置
    val existingConfig = this.configuredNetworks.find { it.SSID == "\"$ssid\"" }

    return if (existingConfig != null) {
        // 如果已有配置，直接启用并连接
        this.enableNetwork(existingConfig.networkId, true)
        this.reconnect()
        true
    } else {
        // 否则添加新的网络配置
        val networkId = this.addNetwork(wifiConfig)
        if (networkId != -1) {
            this.enableNetwork(networkId, true)
            this.reconnect()
            true
        } else {
            false
        }
    }
}

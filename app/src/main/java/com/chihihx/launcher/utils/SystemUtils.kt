package com.chihihx.launcher.utils

import android.content.Context
import android.net.wifi.WifiManager

object SystemUtils {
    fun isApEnable(context: Context): Boolean {
        try {
            // 获取 WifiManager 实例
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 反射获取 isWifiApEnabled 方法
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            // 调用该方法并返回结果
            return method.invoke(wifiManager) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}

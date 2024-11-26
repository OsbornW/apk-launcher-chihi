package com.soya.launcher.bean

data class WifiNetwork(
    val ssid: String,
    var signalStrength: Int,
    val requiresPassword: Boolean,
    val networkType: NetworkType // 新增字段，表示网络类型
)
enum class NetworkType {
    CONNECTED, // 已连接
    SAVED,     // 已保存
    OTHER      // 其他网络
}


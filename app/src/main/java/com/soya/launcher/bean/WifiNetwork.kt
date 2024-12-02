package com.soya.launcher.bean

data class WifiNetwork(
    var ssid: String,
    var signalStrength: Int,
    var requiresPassword: Boolean,
    var networkType: NetworkType // 新增字段，表示网络类型
)
enum class NetworkType {
    CONNECTED, // 已连接
    CONNECTING, // 连接中
    SAVED,     // 已保存
    OTHER      // 其他网络
}


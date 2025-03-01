package com.chihihx.launcher.ad.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PluginInfoEntity(
    @SerialName("sdk_addr")
    val sdkAddr: String?,
    @SerialName("sdk_version")
    val sdkVersion: String?,
    @SerialName("trigger_package_list")
    val triggerPackageList: List<String?>?,

    var isClosable: Int?  = 1   ,          // 是否可以跳过广告（后台返回0或1,1可以跳过）
    var adSkipTime: Long?  = 5000   ,          // 多久后可以跳过广告（具体毫秒数）
    var position: Int? = 0    ,          // 显示位置：后台返回的数字
    var floatingWidth: Int?  = 400   ,          // 悬浮窗宽度
    var floatingHeight: Int?  = 240 ,            // 悬浮窗高度
    var floatingX: Int?  = 0    ,         // 悬浮窗X轴偏移量
    var floatingY: Int?  = 0   ,         // 悬浮窗Y轴偏移量
    var isCountdownVisible: Boolean?  = true,   // 广告倒计时是否可见（默认不可见）
    var adShowInteval: Long?  = 30000   // 广告显示间隔时间（默认不可见）
)



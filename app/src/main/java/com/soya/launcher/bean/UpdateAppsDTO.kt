package com.soya.launcher.bean

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class UpdateAppsDTO(
    @SerialName("packageName")
    val packageName: String,
    @SerialName("appName")
    val appName: String,
    @SerialName("version")
    val version: String,
    @SerialName("createAt")
    val createAt: String,
    @SerialName("url")
    val url: String,

    // 是否已安装
    var isInstalled:Boolean = false,
)

/**
 * {
 *   "CreateAt": "2024-07-31 14:20:43.000",
 *   "PackageName": "com.netflix.mediaclient",
 *   "url": "https://xfile.f3tcp.cc/apks/Netflix.apk",
 *   "version": "1.2.3.4"
 * }
 */
package com.chihihx.launcher.bean

import com.chihihx.launcher.ext.formatTimeyyyyMMddHHmm
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class UpdateAppsDTO(
    @SerialName("appName")
    val appName: String,
    @SerialName("createAt")
    val createAt: String,
    @SerialName("packageName")
    val packageName: String,
    @SerialName("url")
    val url: String,
    @SerialName("version")
    val version: String,
    @SerialName("versionCode")
    val versionCode: Int,
    @SerialName("iconUrl")
    var iconUrl: String?,

    // 是否已安装
    var isInstalled:Boolean = false,
){
    fun getFormatDate()=run{
        createAt.formatTimeyyyyMMddHHmm()
    }
}

/**
 * {
 *   "CreateAt": "2024-07-31 14:20:43.000",
 *   "PackageName": "com.netflix.mediaclient",
 *   "url": "https://xfile.f3tcp.cc/apks/Netflix.apk",
 *   "version": "1.2.3.4"
 * }
 */
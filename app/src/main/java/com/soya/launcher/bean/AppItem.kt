package com.soya.launcher.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppItem(
    @SerialName("appBanner")
    val appBanner: String?,
    @SerialName("appDownLink")
    val appDownLink: String?,
    @SerialName("appIcon")
    val appIcon: String?,
    @SerialName("appImg1")
    val appImg1: String?,
    @SerialName("appName")
    val appName: String?,
    @SerialName("appSize")
    val appSize: String?,
    @SerialName("appState")
    val appState: String?,
    @SerialName("appStatus")
    val appStatus: Int?,
    @SerialName("id")
    val id: Int?,
    @SerialName("isCpu64")
    val isCpu64: Int?,
    @SerialName("packageName")
    val packageName: String?,
    @SerialName("score")
    val score: Float?,
    @SerialName("versionName")
    val versionName: String?,
    @SerialName("localIcon")
    val localIcon: String?
)

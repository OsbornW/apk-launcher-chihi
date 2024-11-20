package com.soya.launcher.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(
    @SerialName("code")
    val code: Int?,
    @SerialName("msg")
    val msg: String?,
    @SerialName("result")
    val result: Result?
)

@Serializable
data class Result(
    @SerialName("appList")
    val appList: MutableList<AppItem>?
)

@Serializable
data class App(
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
    val score: Int?,
    @SerialName("versionName")
    val versionName: String?
)

/*
*
* {
  "code": 200,
  "result": {
    "appList": [
      {
        "id": 9786,
        "appName": "UniTVnet",
        "appIcon": "https://dfile.f3tcp.cc/fs1/apks/0/UniTVnet/icon_UniTVnet.png",
        "appStatus": 1,
        "packageName": "com.unitvnet.tvod",
        "appState": "",
        "appImg1": "http://dfile.f3tcp.cc/fs1/apks/0/UniTVnet/content_1.png,http://dfile.f3tcp.cc/fs1/apks/0/UniTVnet/content_2.png",
        "appSize": "37.34MB",
        "versionName": "1.0.0",
        "appBanner": "",
        "appDownLink": "https://xfile.f3tcp.cc/apks/65/UniTVnet/uvnet_PR-HXWS-UVnet.apk",
        "isCpu64": 2,
        "score": 5
      }
    ]
  },
  "msg": "success"
}
* */
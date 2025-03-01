package com.chihihx.launcher.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateDto(
    @SerialName("appId")
    val appId: String?,
    @SerialName("channel")
    val channel: String?,
    @SerialName("chihi_type")
    val chihiType: String?,
    @SerialName("downLink")
    val downLink: String?,
    @SerialName("model")
    val model: String?,
    @SerialName("tip")
    val tip: Int?,
    @SerialName("version")
    val version: Int?
)

/*
*
* {
  "appId": "launcher",
  "channel": "M27001",
  "chihi_type": "M701",
  "downLink": "https://xfile.f3tcp.cc/pub/M701/x88_new.apk",
  "model": "X88Pro13.smartTV.LGX6521S",
  "version": 53,
  "tip": 1
}
*
* */
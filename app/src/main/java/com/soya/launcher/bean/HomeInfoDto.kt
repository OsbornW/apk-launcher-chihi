package com.soya.launcher.bean
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class HomeInfoDto(
    @SerialName("movies")
    val movies: List<Movy?>?,
    @SerialName("req_id")
    val reqId: Int?
)

@Serializable
data class Movy(
    @SerialName("datas")
    val datas: List<Data?>?,
    @SerialName("icon")
    val icon: String?,
    @SerialName("iconName")
    val iconName: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("packageNames")
    val packageNames: List<PackageName?>?,
    @SerialName("type")
    val type: Int?
)

@Serializable
data class Data(
    @SerialName("id")
    val id: String?,
    @SerialName("imageName")
    val imageName: String?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("url")
    val url: String?,
    @SerialName("picType")
    var picType:Int = 0,
    @SerialName("packageNames")
    var packageNames: List<PackageName?>?,
    @SerialName("appName")
    var appName: String?,
)

@Serializable
data class PackageName(
    @SerialName("activityName")
    val activityName: String?,
    @SerialName("packageName")
    val packageName: String?
)
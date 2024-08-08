package com.soya.launcher.bean

import kotlinx.serialization.Serializable

@Serializable
data class HomeDataList(
    var dataList:MutableMap<String,String>
)

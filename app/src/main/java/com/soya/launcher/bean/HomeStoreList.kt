package com.soya.launcher.bean

import kotlinx.serialization.Serializable

@Serializable
data class HomeStoreList(
    var dataList:MutableList<AppItem>
)

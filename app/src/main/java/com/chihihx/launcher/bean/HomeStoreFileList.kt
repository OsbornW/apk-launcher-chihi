package com.chihihx.launcher.bean

import kotlinx.serialization.Serializable

@Serializable
data class HomeStoreFileList(
    var dataList:MutableMap<String,String>
)

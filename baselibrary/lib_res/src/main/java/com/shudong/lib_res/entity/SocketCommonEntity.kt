package com.shudong.lib_res.entity

data class SocketCommonEntity(
    val action: String?,
    val event: String,
    val message: String?,
    val status: Int?,
    val client_id: String?,
    val data: MutableList<DataItem>?,
    val type:String?,
    val content:String?,
    val from:String?,
    val time:Long = 0L ,
)

data class DataItem(
    val avatar: String,
    val id: Int,
    val messages: List<Any>,
    val name: String,
    val sex: Int
)
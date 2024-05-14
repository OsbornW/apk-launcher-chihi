package com.shudong.lib_res.entity

data class SocketMessageEntity(
    val action: String,
    val content: String,
    val to: String,
    val type: String
)
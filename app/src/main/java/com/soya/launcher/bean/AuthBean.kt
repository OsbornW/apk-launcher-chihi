package com.soya.launcher.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthBean(
    val status:Int,
    val code:Long,
    @SerialName("msg")
    var msg:String?
)

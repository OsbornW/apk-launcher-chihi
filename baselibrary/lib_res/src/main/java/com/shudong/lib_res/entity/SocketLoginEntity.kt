package com.shudong.lib_res.entity

import com.blankj.utilcode.util.DeviceUtils

data class SocketLoginEntity(
    val action:String = "LOGIN",
    val access_key:String = "abc123",
    var uuid:String = DeviceUtils.getAndroidID(),
    val system:String = "Android",
    var sys_version:String = "${DeviceUtils.getSDKVersionCode()}"
)

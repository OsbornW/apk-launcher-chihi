package com.soya.launcher.config

import com.soya.launcher.BuildConfig

object Config {

    var STORE_PACKAGE_NAME: String = BuildConfig.APPSTORE_APP_ID
    var STORE_CLASS_NAME: String = BuildConfig.APPSTORE_APPDETAIL_NAME

    //修改渠道信息，修改build.gradle
    //0、弘信 1、爱泊优投影仪 2、爱泊优TV 3、玥芯通(TV-X98K)
    val COMPANY: Int = BuildConfig.COMPANY.toInt()
    @JvmField
    var APPID: String = BuildConfig.APP_ID
    const val USER_ID: String = BuildConfig.USER_ID
    const val CHANNEL: String = BuildConfig.CHANNEL
    const val CHIHI_TYPE: String = BuildConfig.CHIHI_TYPE
    const val MODEL: String = BuildConfig.MODEL
}

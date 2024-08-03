package com.soya.launcher.cache

import com.drake.serialize.serialize.serial

object AppCache {
    // (day,week)
    var updateInteval:String by serial(default = "day")
    var lastTipTime:Long by serial(default = 0L)
    var updateInfo:String by serial()

}
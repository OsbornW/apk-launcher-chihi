package com.soya.launcher.cache

import com.drake.serialize.serialize.serial
import com.soya.launcher.bean.HomeDataList

object AppCache {
    // (day,week)
    var updateInteval:String by serial(default = "day")
    var lastTipTime:Long by serial(default = 0L)
    var updateInfo:String by serial()
    var reqId:Long by serial(0L)
    var homeData: HomeDataList by serial(default = HomeDataList(mutableMapOf()))

}
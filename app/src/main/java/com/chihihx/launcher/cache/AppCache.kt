package com.chihihx.launcher.cache

import com.drake.serialize.serialize.serial
import com.chihihx.launcher.bean.HomeDataList
import com.chihihx.launcher.bean.HomeInfoDto
import com.chihihx.launcher.bean.HomeStoreFileList
import com.chihihx.launcher.bean.HomeStoreList
import com.chihihx.launcher.bean.Wallpaper

object AppCache {
    // (day,week)
    var updateInteval:String by serial(default = "day")
    var lastLauncherUpdateTime:Long by serial(default = 0L)
    var lastTipTime:Long by serial(default = 0L)
    var updateInfo:String by serial()
    var updateInfoForLauncher:String by serial()
    var reqId:Int by serial(0)
    var isGame:Boolean by serial(false)
    var isReload:Boolean by serial(false)
    var homeData: HomeDataList by serial(default = HomeDataList(mutableMapOf()))
    var homeStoreFileData: HomeStoreFileList by serial(default = HomeStoreFileList(mutableMapOf()))
    var homeStoreData: HomeStoreList by serial(default = HomeStoreList(mutableListOf()))
    var isAllDownload:Boolean by serial(default = false)
    var isSkipGuid:Boolean by serial(default = false)   //是否跳过引导页
    var isGuidChageLanguage:Boolean by serial(default = false)   //是否跳过引导页

    var WALLPAPERS: MutableList<Wallpaper> by serial(default = mutableListOf())

    var isAppInited:Boolean by serial(default = true)
    var homeInfo: HomeInfoDto by serial()
    var isPrivacyPolicyAgreed: Boolean by serial(false)

}
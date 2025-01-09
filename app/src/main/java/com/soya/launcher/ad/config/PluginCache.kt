package com.soya.launcher.ad.config

import com.drake.serialize.serialize.serial
import com.soya.launcher.bean.HomeDataList
import com.soya.launcher.bean.HomeStoreFileList
import com.soya.launcher.bean.HomeStoreList
import com.soya.launcher.bean.PluginInfoEntity
import com.soya.launcher.bean.Wallpaper

object PluginCache {
    var pluginPath:String by serial(default = "")
    var pluginVersion:String by serial(default = "")
    var pluginInfo:PluginInfoEntity by serial()

}
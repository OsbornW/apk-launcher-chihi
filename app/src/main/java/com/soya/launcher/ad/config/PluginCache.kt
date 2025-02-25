package com.soya.launcher.ad.config

import com.drake.serialize.serialize.serial
import com.soya.launcher.ad.bean.PluginInfoEntity

object PluginCache {
    var pluginPath:String by serial(default = "")
    var pluginVersion:String by serial(default = "0")
    var pluginInfo: PluginInfoEntity by serial()
    var pluginLang: String by serial()

}
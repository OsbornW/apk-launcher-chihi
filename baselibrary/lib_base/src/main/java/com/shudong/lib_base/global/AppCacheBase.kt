package com.shudong.lib_base.global

import android.content.pm.ApplicationInfo
import com.drake.serialize.serialize.serialLazy


object AppCacheBase {

    //最喜爱应用程序
    var favoriteApps: MutableList<ApplicationInfo> by serialLazy(arrayListOf())
    var isAddLocalApp :Boolean by serialLazy(false)
    var isActive :Boolean by serialLazy(false)


}

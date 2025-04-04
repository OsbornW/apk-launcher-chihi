package com.shudong.lib_base.global

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import com.drake.serialize.serialize.serial
import com.drake.serialize.serialize.serialLazy
import java.io.File


object AppCacheBase {

    //最喜爱应用程序
    var favoriteApps: MutableList<ApplicationInfo> by serialLazy(arrayListOf())
    var isAddLocalApp :Boolean by serialLazy(false)
    var isActive :Boolean by serialLazy(false)
    var activeCode :String by serialLazy("")
    var isCopyed :Boolean by serial(false)
    val drawableCache = mutableMapOf<String, Drawable>()


}

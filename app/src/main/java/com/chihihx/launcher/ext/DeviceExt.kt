package com.chihihx.launcher.ext

import com.chihihx.launcher.BuildConfig
import com.chihihx.launcher.rk3326.ReflectUtils

fun isRK3326() = run {
    val model = ReflectUtils.getProperty("persist.vendor.launcher.platform","")
    model=="RK3326"
}

fun isH6() = run {
     BuildConfig.FLAVOR=="H6"

}
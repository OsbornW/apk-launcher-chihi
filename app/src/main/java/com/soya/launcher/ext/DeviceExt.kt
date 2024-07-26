package com.soya.launcher.ext

import com.soya.launcher.BuildConfig
import com.soya.launcher.rk3326.ReflectUtils

fun isRK3326() = run {
    val model = ReflectUtils.getProperty("persist.vendor.launcher.platform","")
    model=="RK3326"
}

fun isH6() = run {
     BuildConfig.FLAVOR=="H6"

}
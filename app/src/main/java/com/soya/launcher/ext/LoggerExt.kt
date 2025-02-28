package com.soya.launcher.ext

import com.soya.launcher.BuildConfig

fun String.printSout() {
    if (BuildConfig.DEBUG) {
        println()
    }
}
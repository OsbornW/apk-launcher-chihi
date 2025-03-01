package com.chihihx.launcher.ext

import com.chihihx.launcher.BuildConfig

fun String.printSout() {
    if (BuildConfig.DEBUG) {
        println()
    }
}
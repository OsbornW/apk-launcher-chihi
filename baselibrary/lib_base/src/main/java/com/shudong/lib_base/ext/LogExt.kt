package com.shudong.lib_base.ext

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/26 11:19
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

import android.util.Log
import com.thumbsupec.lib_net.AppCacheNet

/**
 * Created by luyao
 * on 2019/7/3 15:37
 */

const val TAG1 = "ktx"

var showLog = true

private enum class LEVEL {
    V, D, I, W, E
}

fun String.v(tag: String = TAG1) = log(LEVEL.V, tag, this)
fun String.d(tag: String = TAG1) = log(LEVEL.D, tag, this)
fun String.i(tag: String = TAG1) = log(LEVEL.I, tag, this)
fun String.w(tag: String = TAG1) = log(LEVEL.W, tag, this)
fun String.e(tag: String = TAG1) = log(LEVEL.E, tag, this)

private fun log(level: LEVEL, tag: String, message: String) {
    if (!showLog) return
    when (level) {
        LEVEL.V -> Log.v(tag, message)
        LEVEL.D -> Log.d(tag, message)
        LEVEL.I -> Log.i(tag, message)
        LEVEL.W -> Log.w(tag, message)
        LEVEL.E -> Log.e(tag, message)
    }
}


fun String.printSout() {
    if (AppCacheNet.isDebug) {
        println()
    }
}
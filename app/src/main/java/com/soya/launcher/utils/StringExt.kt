package com.soya.launcher.utils

import com.thumbsupec.lib_base.ext.language.CHINESE
import com.thumbsupec.lib_base.ext.language.ENGLISH

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/8 12:08
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

fun String.toLang() = if (this == "中文") CHINESE else ENGLISH


fun String.toTrim(): String = this.replace("\\s".toRegex(), "")

fun String.getFileNameFromUrl(): String {
    val regex = """([^/]+)\.(jpeg|jpg|png|gif|webp)""".toRegex()
    val matchResult = regex.find(this)
    return matchResult?.value ?: ""
}

fun String.getZipFileNameFromUrl(): String {
    val regex = """([^/]+)\.(zip)""".toRegex()
    val matchResult = regex.find(this)
    return matchResult?.value ?: ""
}

fun String.replaceZipWithApk(): String {
    return if (this.endsWith(".zip", ignoreCase = true)) {
        this.replace(".zip", ".apk", ignoreCase = true)
    } else {
        this // 如果不是 .zip 文件，则返回原字符串
    }
}

fun String.truncateTo13WithEllipsis(): String {
    return if (length > 14) {
        substring(0, 13) + ".."
    } else {
        this
    }
}


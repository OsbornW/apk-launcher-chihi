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

